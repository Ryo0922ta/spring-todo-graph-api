$(async function () {
    await taskLoad();
});

// async はこのメソッドは返り値にpromiseを返すよという宣言
// await　はpromiseに結果が返るまで待つという宣言
async function taskLoad() {
    const taskList = await taskfetch();
    taskList.forEach((task) => {
        const taskCard = createTaskCard(task);

        switch (task.stateId) {
            case 1:
                $("#todoList").append(taskCard);
                break;
            case 2:
                $("#inProgressList").append(taskCard);
                break;
            case 3:
                $("#doneList").append(taskCard);
                break;
        }
    });
}

function createTaskCard(task) {
    return $(`
			<div class="task-card" data-task-id="${task.taskId}">
				<div class="task-title">
					${escapeHtml(task.taskName)}
				</div>
				<div class="task-meta">
					<span class="task-badge urgency-badge">緊急度: ${task.urgency}</span>
					<span class="task-badge importance-badge">重要度: ${task.importance}</span>
				</div>
				<div class="task-action">
					<button class="delete-btn" data-task-id="${task.id}">削除</button>
				</div>
            </div>
		`);
}

async function taskfetch() {
    try {
        const response = await fetch(`http://localhost:8080/api/tasks`);

        const data = await response.json();
        console.log("タスクの読み込み成功", data);
        return data;
    } catch (error) {
        console.log("失敗", error);
    }
}

// HTMLエスケープ クロスサイトスクリプティングの防止につながる。
function escapeHtml(text) {
    const map = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;",
    };
    return text.replace(/[&<>"']/g, (m) => map[m]);
}
