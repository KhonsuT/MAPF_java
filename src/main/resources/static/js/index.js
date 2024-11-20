// Load SockJS dynamically
var sockJsScript = document.createElement('script');
sockJsScript.src = "https://cdn.jsdelivr.net/npm/sockjs-client@1.5.2/dist/sockjs.min.js";
document.head.appendChild(sockJsScript);

// Dynamically load StompJS library
sockJsScript.onload = function () {
    var stompJsScript = document.createElement('script');
    stompJsScript.src = "https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js";
    document.head.appendChild(stompJsScript);
};

// Maintain list of available agents
let availableAgents = [1, 2, 3, 4, 5, 6, 7, 8, 9];

document.addEventListener("DOMContentLoaded", function () {
    // Fetch initial map state
    fetch('/api/getMap')
        .then(response => response.json())
        .then(grid => {
            updateGrid(grid);
        })
        .catch(error => console.error('Error fetching initial grid:', error));

    // WebSocket logic
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/mapState', function (messageOutput) {
            var grid = JSON.parse(messageOutput.body);
            updateGrid(grid);
        });
    }, function (error) {
        console.error('Error in WebSocket connection:', error);
    });

    // Button listeners (Start and Re-run)
    document.getElementById("resetButton").addEventListener("click", function () {
        fetch('/api/reset')
            .then(response => response.text())
            .catch(error => console.error('Error triggering re-run:', error));
        availableAgents = [1, 2, 3, 4, 5, 6, 7, 8, 9];
    });

    document.getElementById("startButton").addEventListener("click", function () {
        fetch('/api/start')
            .then(response => response.text())
            .catch(error => console.error('Error triggering start:', error));
    });

    // Map size update listener
    document.getElementById("mapSizeForm").addEventListener("submit", function (event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        if (formData.get('width')>20 || formData.get('height')>20) {
        document.getElementById("mapCaption").innerHTML = "Doesn't support map size over 20.";
        return;}
        else {
            document.getElementById("mapCaption").innerHTML = "";
        }
        const data = {
            width: formData.get('width'),
            height: formData.get('height')
        };

        fetch('/api/updateMapSize', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
        .catch(error => console.error('Error updating map size:', error));
    });
});

// Function to update the grid display in HTML
function updateGrid(grid) {
    const mapContainer = document.getElementById("map"); // Ensure this element exists in your HTML
    mapContainer.innerHTML = ''; // Clear previous content

    // Create the header row for column labels (for readability)
    const headerRow = document.createElement('div');
    headerRow.classList.add('row');
    headerRow.style.display = 'flex'; // Flexbox layout

    // Add an empty corner cell for alignment with row labels
    const cornerCell = document.createElement('div');
    cornerCell.classList.add('cell', 'corner'); // Optional 'corner' class for styling
    cornerCell.style.width = '20px'; // Adjust as needed
    headerRow.appendChild(cornerCell);

    // Add column labels (0, 1, 2, 3, ...)
    for (let col = 0; col < grid[0].length; col++) {
        const colLabel = document.createElement('div');
        colLabel.classList.add('cell', 'label'); // Optional 'label' class for styling
        colLabel.textContent = col; // Column numbers start at 0
        headerRow.appendChild(colLabel);
    }

    mapContainer.appendChild(headerRow);

    // Create rows for the grid with row labels
    grid.forEach(function (row, rowIndex) {
        const rowElement = document.createElement('div');
        rowElement.classList.add('row');
        rowElement.style.display = 'flex'; // Use flexbox to lay out cells horizontally

        // Add row label
        const rowLabel = document.createElement('div');
        rowLabel.classList.add('cell', 'label'); // Optional 'label' class for styling
        rowLabel.textContent = rowIndex; // Row numbers start at 0
        rowLabel.style.width = '20px'; // Adjust as needed
        rowElement.appendChild(rowLabel);

        // Add cells for the grid
        row.forEach(function (cell, colIndex) {
            const cellElement = document.createElement('div');
            cellElement.classList.add('cell');

            // Assign specific classes based on cell type (empty, obstacle, or agent)
            if (cell === -1) {
                cellElement.classList.add('obstacle');
            } else if (cell === 0) {
                cellElement.classList.add('empty');
            } else if (cell >= 1 && cell <= 9) {
                cellElement.classList.add('number-' + cell);
            }

            // Attach event listeners for left-click (add/remove agent) and right-click (add/remove obstacle)
            cellElement.addEventListener('click', function () {
                handleAgentToggle(rowIndex, colIndex, cell, cellElement);
            });

            cellElement.addEventListener('contextmenu', function (event) {
                event.preventDefault(); // Prevent the default right-click menu
                handleObstacleToggle(rowIndex, colIndex, cell, cellElement);
            });

            rowElement.appendChild(cellElement); // Append cell to the row
        });

        mapContainer.appendChild(rowElement); // Append row to the map container
    });
}

// Toggle agent on left click
function handleAgentToggle(row, col, cellValue, cellElement) {
    if (cellValue >= 1 && cellValue <= 9) {
        // Remove agent
        const agentID = cellValue;
        console.log(`Removing agent ${agentID} at (${row}, ${col})`);
        availableAgents.push(agentID); // Return agent to list
        availableAgents.sort(); // Keep list sorted

        fetch('/api/removeAgent', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ agentID: agentID })
        })
        .then(response => response.text())
        .catch(error => console.error('Error removing agent:', error));

        cellElement.className = 'cell empty'; // Update display
    } else {
        // Add agent
        if (availableAgents.length === 0) {
            console.log('No available agents to add!');
            return;
        }
        const agentID = availableAgents.shift(); // Get next available agent
        console.log(`Adding agent ${agentID} at (${row}, ${col})`);

        fetch('/api/addAgent', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ agentID: [agentID], pos: [row, col] })
        })
        .then(response => response.text())
        .catch(error => console.error('Error adding agent:', error));

        cellElement.className = 'cell number-' + agentID; // Update display
    }
}

// Toggle obstacle on right click
function handleObstacleToggle(row, col, cellValue, cellElement) {
    if (cellValue === -1) {
        // Remove obstacle
        console.log(`Removing obstacle at (${row}, ${col})`);

        fetch('/api/removeObstacle', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ pos: [row,col] })
        })
        .then(response => response.text())
        .catch(error => console.error('Error removing obstacle:', error));

        cellElement.className = 'cell empty'; // Update display
    } else {
        // Add obstacle
        console.log(`Adding obstacle at (${row}, ${col})`);

        fetch('/api/addObstacle', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ pos: [row, col] })
        })
        .then(response => response.text())
        .catch(error => console.error('Error adding obstacle:', error));

        cellElement.className = 'cell obstacle'; // Update display
    }
}

