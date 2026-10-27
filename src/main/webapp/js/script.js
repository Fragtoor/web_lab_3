let currentR = null;

const canvas = document.getElementById('graphCanvas');
const ctx = canvas.getContext('2d');
const rVisual = 140;
const cx = canvas.width / 2;
const cy = canvas.height / 2;

function drawGraph(rLabel) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#3399FF';
    ctx.strokeStyle = '#000';

    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.arc(cx, cy, rVisual / 2, 0, -0.5 * Math.PI, true);
    ctx.closePath();
    ctx.fill();

    ctx.fillRect(cx - rVisual, cy - rVisual, rVisual, rVisual);

    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(cx - rVisual, cy);
    ctx.lineTo(cx, cy + rVisual);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(0, cy);
    ctx.lineTo(canvas.width, cy);
    ctx.moveTo(cx, 0);
    ctx.lineTo(cx, canvas.height);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(canvas.width - 10, cy - 5);
    ctx.lineTo(canvas.width, cy);
    ctx.lineTo(canvas.width - 10, cy + 5);
    ctx.moveTo(cx - 5, 10);
    ctx.lineTo(cx, 0);
    ctx.lineTo(cx + 5, 10);
    ctx.stroke();

    const ticks = [-rVisual, -rVisual / 2, rVisual / 2, rVisual];
    ticks.forEach(t => {
        ctx.beginPath();
        ctx.moveTo(cx + t, cy - 4);
        ctx.lineTo(cx + t, cy + 4);
        ctx.moveTo(cx - 4, cy + t);
        ctx.lineTo(cx + 4, cy + t);
        ctx.stroke();
    });

    ctx.fillStyle = '#000';
    ctx.font = '14px sans-serif';
    ctx.fillText('x', canvas.width - 15, cy - 15);
    ctx.fillText('y', cx + 15, 15);

    const lblR = rLabel ? String(rLabel) : 'R';
    const lblHalfR = rLabel ? String(rLabel / 2) : 'R/2';

    ctx.fillText(lblR, cx + rVisual - 5, cy + 18);
    ctx.fillText(lblHalfR, cx + rVisual / 2 - 10, cy + 18);
    ctx.fillText('-' + lblHalfR, cx - rVisual / 2 - 15, cy + 18);
    ctx.fillText('-' + lblR, cx - rVisual - 10, cy + 18);

    ctx.fillText(lblR, cx + 10, cy - rVisual + 5);
    ctx.fillText(lblHalfR, cx + 10, cy - rVisual / 2 + 5);
    ctx.fillText('-' + lblHalfR, cx + 10, cy + rVisual / 2 + 5);
    ctx.fillText('-' + lblR, cx + 10, cy + rVisual + 5);
}

function drawPoint(x, y, r, isHit) {
    const activeRadius = currentR !== null ? currentR : r;
    const xPx = cx + (x / activeRadius) * rVisual;
    const yPx = cy - (y / activeRadius) * rVisual;

    ctx.beginPath();
    ctx.arc(xPx, yPx, 4, 0, 2 * Math.PI);
    ctx.fillStyle = isHit ? '#28a745' : '#dc3545';
    ctx.fill();
    ctx.strokeStyle = '#000';
    ctx.stroke();
    ctx.closePath();
}

function redrawAll() {
    drawGraph(currentR);
    if (Array.isArray(SERVER_POINTS)) {
        SERVER_POINTS.forEach(pt => drawPoint(pt.x, pt.y, pt.r, pt.hit));
    }
}

window.setR = function(val, btnElement) {
    currentR = val;
    document.getElementById('r-val').value = val;
    document.getElementById('r-error').style.display = 'none';
    document.getElementById('canvas-error').style.display = 'none';

    sessionStorage.setItem('selectedR', val);

    const btns = document.querySelectorAll('.r-btn');
    btns.forEach(btn => btn.classList.remove('active'));
    btnElement.classList.add('active');

    redrawAll();
};

function initTable() {
    const tbody = document.getElementById("results-body");
    if (!tbody) return;
    tbody.innerHTML = "";

    if (Array.isArray(SERVER_POINTS)) {
        SERVER_POINTS.forEach(row => {
            let localTime = row.time;
            const dateObj = new Date(row.time);
            if (!isNaN(dateObj.getTime())) {
                localTime = new Intl.DateTimeFormat('ru-RU', {
                    year: 'numeric', month: '2-digit', day: '2-digit',
                    hour: '2-digit', minute: '2-digit', second: '2-digit'
                }).format(dateObj);
            }

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${row.x}</td>
                <td>${row.y}</td>
                <td>${row.r}</td>
                <td style="color: ${row.hit ? 'green' : 'red'}; font-weight: bold;">${row.hit ? "Попадание" : "Промах"}</td>
                <td>${localTime}</td>
                <td>${row.exec_time}</td>
            `;
            tbody.appendChild(tr);
        });
    }
}

document.getElementById('pointForm').addEventListener('submit', (e) => {
    document.querySelectorAll('.error').forEach(el => el.style.display = 'none');
    let isValid = true;

    const checkedX = document.querySelectorAll('input[name="x"]:checked');
    if (checkedX.length === 0) {
        document.getElementById('x-error').style.display = 'block';
        isValid = false;
    }

    const rawY = document.getElementById('y-val').value.trim().replace(',', '.');
    const yVal = Number(rawY);
    if (rawY === "" || isNaN(yVal) || yVal <= -3 || yVal >= 3) {
        document.getElementById('y-error').style.display = 'block';
        isValid = false;
    } else {
        document.getElementById('y-val').value = rawY;
    }

    if (currentR === null || document.getElementById('r-val').value === "") {
        document.getElementById('r-error').style.display = 'block';
        isValid = false;
    }
    if (!isValid) {
        e.preventDefault();
    }
});

canvas.addEventListener('click', (event) => {
    document.getElementById('canvas-error').style.display = 'none';

    if (currentR === null) {
        document.getElementById('canvas-error').style.display = 'block';
        return;
    }

    const rect = canvas.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;

    const mathX = (((clickX - cx) / rVisual) * currentR).toFixed(4);
    const mathY = (((cy - clickY) / rVisual) * currentR).toFixed(4);

    window.location.href = `${CONTEXT_PATH}/controller?x=${encodeURIComponent(mathX)}&y=${encodeURIComponent(mathY)}&r=${encodeURIComponent(currentR)}`;
});

document.getElementById('clearBtn').addEventListener('click', () => {
    window.location.href = `${CONTEXT_PATH}/controller?action=clear`;
});

window.addEventListener('load', () => {
    initTable();

    const savedR = sessionStorage.getItem('selectedR');
    if (savedR) {
        const btns = document.querySelectorAll('.r-btn');
        btns.forEach(btn => {
            if (btn.value === savedR) {
                setR(parseFloat(savedR), btn);
            }
        });
    } else {
        redrawAll();
    }
});
