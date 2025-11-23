$(async function () {
    await allTaskLoad();
});

// ##イベントロジック
$("#addTaskBtn").on("click", async function () {
    await addTask();
});

//動的な要素の場合は"document"に渡す。
// 以下の処理はdocumentに伝播したクリックイベントのうち
// event.targetが"deleteTaskBtn"にマッチする時だけ処理を実行する
$(document).on("click", ".deleteTaskBtn", async function () {
    const taskId = $(this).data("task-id");
    await deleteTask(taskId, this);
});

$(document).on("click", ".openTaskDetailMdlBtn", async function () {
    const taskId = $(this).data("task-id");
    await taskLoad(taskId);
    openModal();
});

$(".close-mdl-btn , #overlay").on("click", function () {
    closeModal();
});

$(".edit-action-btn").on("click", async function () {
    await updateTask();
    closeModal();
});

// モーダル内部クリック → overlayへ伝播させない
$(".taskDetailMdl").on("click", function (event) {
    event.stopPropagation();
});

// ##メインロジック
// async はこのメソッドは返り値にpromiseを返すよという宣言
// await　はpromiseに結果が返るまで待つという宣言
async function allTaskLoad() {
    const taskList = await allTaskFetch();
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

async function taskLoad(taskId) {
    const response = await taskfetch(taskId);
    $("input.editTaskName").val(response.taskName);
    $("input.editUrgency").val(response.urgency);
    $("input.editImportance").val(response.importance);
    $("input.editTaskId").val(response.taskId);
}

// ##メインロジック　btnロジック
//idタグを指定しているのか,inputタグなどを指定しているのかで記述方法が違う。
// 終端と内部の引用符を区別　””：文字列リテラル, '':CSSの属性セレクタ
async function addTask() {
    try {
        alertInputEmpty();

        const payload = {
            taskName: $("input[name='taskName']").val(),
            importance: parseInt($("input[name='importance']").val()),
            urgency: parseInt($("input[name='urgency']").val()),
            userId: 1,
            stateId: null,
        };

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
        httpErrorHandler(jqXHR);
    }
}

async function deleteTask(taskId, btnElement) {
    try {
        if (!confirm("本当に削除しますか？")) {
            return;
        }

        await $.ajax({
            url: `http://localhost:8080/api/tasks/${taskId}`,
            method: "DELETE",
        });

        $(btnElement).closest(".task-card").remove();
    } catch (jqXHR) {
        httpErrorHandler(jqXHR);
    }
}

async function updateTask() {
    try {
        taskId = $("input[name='editedTaskId']").val();

        const editedpayload = {
            taskId: taskId,
            taskName: $("input[name='editedTaskName']").val(),
            urgency: parseInt($("input[name='editedUrgency']").val()),
            importance: parseInt($("input[name='editedImportance']").val()),
            userId: 1,
            stateId: null,
        };
        //リロードしたら更新かかっていたのになぜかエラーに引っかかってた。。apiが正しくresponse出来てなかった。
        const updatedTask = await $.ajax({
            url: `http://localhost:8080/api/tasks/${taskId}`,
            method: "PUT",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(editedpayload),
        });

        //　一覧表示や追加と違って既存のタスクカードを更新しないといけない
        const $card = $(`.task-card[data-task-id="${taskId}"]`);
        const updateTaskCard = createTaskCard(updatedTask);

        // console.log("updateTask", updatedTask);
        switch (updatedTask.stateId) {
            case 1:
                $card.replaceWith(updateTaskCard);
                break;
            case 2:
                $card.replaceWith(updateTaskCard);
                break;
            case 3:
                $card.replaceWith(updateTaskCard);
                break;
        }
    } catch (jqXHR) {
        httpErrorHandler(jqXHR);
    }
}

// ##ビジネスロジック
// fetch()より$.ajaxを使う方が良い。エラーハンドリングが超優秀
async function allTaskFetch() {
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
        httpErrorHandler(jqXHR);
        return []; //もし仮にエラーでも[].foreachが実行されるので後続処理で画面崩れが起きにくい。
    }
}
async function taskfetch(taskId) {
    try {
        const response = await $.ajax({
            url: `http://localhost:8080/api/tasks/${taskId}`,
            method: "GET",
            dataType: "json",
            timeout: 5000,
        });
        return response;
    } catch (jqXHR) {
        httpErrorHandler(jqXHR);
        return []; //もし仮にエラーでも[].foreachが実行されるので後続処理で画面崩れが起きにくい。
    }
}

// モーダル表示
function openModal() {
    console.log("openModalは呼ばれてるよ");
    $("#overlay, .taskDetailMdl").fadeIn(); //" , " の書き方注意
}

// モーダル非表示
function closeModal() {
    $("#overlay, .taskDetailMdl").fadeOut();
}

// ##UI生成
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
					<button class="openTaskDetailMdlBtn" data-task-id="${task.taskId}">編集</button>
					<button class="deleteTaskBtn" data-task-id="${task.taskId}">削除</button>
				</div>
			</div>
		`);
}

// ##共通関数
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

function alertInputEmpty() {
    if ($("input[name='taskName']").val() === "") {
        alert("タスク名の空欄はやめて下さい");
        return;
    }
}

function httpErrorHandler(jqXHR) {
    if (jqXHR.status === 0) {
        alert("サーバーに接続できません。サーバーの状態を確認して下さい");
    } else if (jqXHR.status === 400) {
        alert("リクエスト先が不正です");
    } else if (jqXHR.status === 404) {
        alert("APIが見つかりません。");
    } else if (jqXHR.status === 500) {
        alert("apiの実装またはdbサーバーを確認して下さい");
    } else {
        alert(
            "予期せぬエラーが発生しました。apiのレスポンスが正しいか確認してください。"
        );
    }
}
