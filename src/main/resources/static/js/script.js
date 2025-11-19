$(async function () {
    await taskLoad();
});

$("#addTaskBtn").on("click", async function () {
    await addTaskBtn();
});
//idタグを指定しているのか,inputタグなどを指定しているのかで記述方法が違う。
// 終端と内部の引用符を区別　””：文字列リテラル, '':CSSの属性セレクタ
async function addTaskBtn() {
    try {
        if ($("input[name='taskName']").val() === "") {
            alert("タスク名の空欄はやめて下さい");
            return;
        }
        const payload = {
            taskName: $("input[name='taskName']").val(),
            importance: parseInt($("input[name='importance']").val()),
            urgency: parseInt($("input[name='urgency']").val()),
            userId: 1,
            stateId: null,
        };
        console.log("payloadの中身どんなもん？ : ", payload);

        const newTask = await $.ajax({
            url: "http://localhost:8080/api/tasks",
            method: "POST",
            contentType: "application/json", // サーバ側にjson文字列を送ることを宣言
            dataType: "json",
            data: JSON.stringify(payload), // jsのオブジェクト型をjson形式に変換する
        });

        const newTaskCard = createTaskCard(newTask);
        switch (newTask.stateId) {
            case 1:
                $("#todoList").append(newTaskCard);
                break;
            case 2:
                $("#inProgressList").append(newTaskCard);
                break;
            case 3:
                $("#doneList").append(newTaskCard);
                break;
        }
        $("input[name='taskName']").val("");
    } catch (jqXHR) {
        console.log("失敗", jqXHR);

        if (jqXHR.status === 0) {
            alert("サーバーに接続できません。サーバーの状態を確認して下さい");
        } else if (jqXHR.status === 400) {
            alert("リクエスト先が不正です");
        } else if (jqXHR.status === 404) {
            alert("APIが見つかりません。");
        } else if (jqXHR.status === 500) {
            alert("apiの実装またはdbサーバーを確認して下さい");
        } else {
            alert("予期せぬエラーが発生しました。");
        }
        return []; //もし仮にエラーでも[].foreachが実行されるので後続処理で画面崩れが起きにくい。
    }
}

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

// fetch()より$.ajaxを使う方が良い。エラーハンドリングが超優秀
async function taskfetch() {
    try {
        const response = await $.ajax({
            url: "http://localhost:8080/api/tasks",
            method: "GET",
            dataType: "json",
            timeout: 5000,
        });
        // .ajaxだとdataTypeで直接jsonを指定できるからjson()不要
        // console.log("タスクの読み込み成功", response);
        return response;
    } catch (jqXHR) {
        console.log("失敗", jqXHR);

        if (jqXHR.status === 0) {
            alert("サーバーに接続できません。サーバーの状態を確認して下さい");
        } else if (jqXHR.status === 400) {
            alert("リクエスト先が不正です");
        } else if (jqXHR.status === 404) {
            alert("APIが見つかりません。");
        } else if (jqXHR.status === 500) {
            alert("apiの実装またはdbサーバーを確認して下さい");
        } else {
            alert("予期せぬエラーが発生しました。");
        }
        return []; //もし仮にエラーでも[].foreachが実行されるので後続処理で画面崩れが起きにくい。
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
