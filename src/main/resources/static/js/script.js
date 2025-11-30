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
    const taskId = $(this).data("task-id");
    console.log(taskId);
    await updateTask(taskId);
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
        renderScatter(task);
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
    makeDraggable();
    makeDroppable(taskList);

    return taskList;
}

async function taskLoad(taskId) {
    const response = await taskfetch(taskId);
    $("input.editTaskName").val(response.taskName);
    $("input.editUrgency").val(response.urgency);
    $("input.editImportance").val(response.importance);
    $(".edit-action-btn").attr("data-task-id", response.taskId);
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
        renderScatter(newTask);
        const newTaskCard = createTaskCard(newTask);
        switch (newTask.stateId) {
            case 1:
                $("#todoList").append(newTaskCard);
                break; //breakはswitchから抜けるだけ
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
    makeDraggable();
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
        $(`.chart-point[data-task-id="${taskId}"]`).remove();
    } catch (jqXHR) {
        httpErrorHandler(jqXHR);
    }
}

async function updateTask(taskId, stateId = null) {
    try {
        taskName = normalizeValue($("input[name='editedTaskName']").val());
        urgency = normalizeValue($("input[name='editedUrgency']").val());
        importance = normalizeValue($("input[name='editedImportance']").val());

        //　一覧表示や追加と違って既存のタスクカードを更新しないといけない

        stateId = normalizeValue(stateId);

        const editedpayload = {
            taskId: taskId,
            taskName: taskName,
            urgency: urgency,
            importance: importance,
            userId: 1,
            stateId: stateId,
        };
        // リロードしたら更新かかっていたのになぜかエラーに引っかかってた。。apiが正しくresponse出来てなかった。
        const updatedTask = await $.ajax({
            url: `http://localhost:8080/api/tasks/${taskId}`,
            method: "PUT",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(editedpayload),
        });
        $(`.chart-point[data-task-id="${taskId}"]`).remove();
        renderScatter(updatedTask);
        const $card = $(`.task-card[data-task-id="${taskId}"]`);
        if (stateId === null) {
            $card.find(".task-title").text(updatedTask.taskName);
            $card.find(".urgency-badge").text(`緊急度: ${updatedTask.urgency}`);
            $card
                .find(".importance-badge")
                .text(`重要度: ${updatedTask.importance}`);
            makeDraggable();
            return;
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
    $("#overlay, .taskDetailMdl").fadeIn(); //" , " の書き方注意
}

// モーダル非表示
function closeModal() {
    $("#overlay, .taskDetailMdl").fadeOut();
}

function makeDraggable() {
    // drag可能にする
    $(".task-card").draggable({
        //helperがないとdrag中のtop leftがそのまま残ってしまう。
        helper: "clone",
        revert: "invalid",
        cursor: "move",
        zIndex: 1000,
    });
}

function makeDroppable(taskList) {
    const stateNameToStateId = {
        todo: 1,
        doing: 2,
        done: 3,
    };
    // console.log("tasklist", taskList);
    //drop可能にする
    $(".task-list").droppable({
        accept: function (draggable) {
            const fromState = $(draggable)
                .closest(".task-list")
                .parent()
                .data("status");
            const toState = $(this).parent().data("status");
            return fromState !== toState;
        },
        hoverClass: "ui-state-hover",
        drop: async function (event, ui) {
            const $droppedCard = ui.draggable;
            const $originalList = $droppedCard.closest(".task-list");

            targetTaskId = parseInt(ui.draggable.data("task-id"));
            newTaskStatusId =
                stateNameToStateId[$(this).parent().data("status")];

            $(this).append($droppedCard);

            try {
                await updateTask(targetTaskId, newTaskStatusId);
            } catch (e) {
                alert("更新に失敗したのでUIを戻します。");
                // ui.draggable.closest(".task-list").append($droppedCard);
                // このコードでは元の位置にはならない。append後の処理だから closest()が位置しないところを指してしまう
                $originalList.append($droppedCard);
            }
        },
    });
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

function renderScatter(task) {
    const stateIdtoStateName = {
        1: "todo",
        2: "doing",
        3: "done",
    };

    const chart = $("#scatter-chart");
    // chart.find(".chart-point").remove();
    const chartWidth = chart.width() * 0.8;
    const chartHeight = chart.height() * 0.8;

    const x = (task.urgency / 10) * chartWidth + 0.1 * chartWidth;
    const y =
        chartHeight - (task.importance / 10) * chartHeight + 0.1 * chartHeight;
    //(task.importance / 10) chartHeight 重要度の値から求めた値
    // 0.1 * chartHeight　topを10%と設定しているからその分の高さ

    const point = $(
        `<div class="chart-point ${
            stateIdtoStateName[task.stateId]
        }"data-task-id="${task.taskId}"></div>`
    );

    point.css({
        left: x + "px",
        top: y + "px",
    });

    point.hover(
        function (e) {
            const tooltip = $(".tooltip");

            tooltip.html(
                `<div>
                <strong>
                タイトル : ${task.taskName}
                </strong></br>
                重要度(y軸):${task.importance} | 緊急度(x軸):${
                    task.urgency
                }</br>
                タスクの状態:${stateIdtoStateName[task.stateId]}</br>
                </div>`
            );

            const windowWidth = $(window).width();
            const tooltipWidth = tooltip.outerWidth(); //ここで定義しないと想定されるwidthが取れない
            let tooltipX = e.pageX + 10;
            const tooltipY = e.pageY + 10;

            if (tooltipX + tooltipWidth > windowWidth) {
                tooltipX = e.pageX - tooltipWidth - 10; // constで定義していたから再代入するなで怒られた。　Assignment to constant variable.
            }

            tooltip.css({
                display: "block",
                left: tooltipX + "px",
                top: tooltipY + "px",
            });
        },
        function () {
            $(".tooltip").css({ display: "none" }); //これじゃダメな理由がわからない ->  セレクタとnoneの渡し方が良くなかっただけだった。
        }
    );

    chart.append(point);
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

function normalizeValue(value) {
    if (value == null) {
        // null or undefineを検知
        return null;
    }
    if (typeof value == "number") {
        // 数字
        return value;
    }
    if (typeof value == "string" && value.trim() === "") {
        /// "  "　などの空白を検知
        return null;
    }

    return value;
}

$(window).resize(function () {
    
});
