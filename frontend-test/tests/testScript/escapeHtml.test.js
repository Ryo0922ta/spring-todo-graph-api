const escapeHtml = require("../script/todo-graph-script.js");

describe("escapeHtml の単体テスト", () => {
    test("基本的な記号を正しくエスケープできる ", () => {
        expect(escapeHtml("&")).toBe("&amp;");
        expect(escapeHtml("<")).toBe("&lt;");
        expect(escapeHtml(">")).toBe("&gt;");
        expect(escapeHtml('"')).toBe("&quot;"); //' " '
        expect(escapeHtml("'")).toBe("&#039;");
    });

    test("複数の危険な記号を含む文字列を想定通りエスケープできること", () => {
        const testStr = `Tom<script>alert("xss")</script>hello`;
        const expected = `Tom&lt;script&gt;alert(&quot;xss&quot;)&lt;/script&gt;hello`;

        expect(escapeHtml(testStr)).toBe(expected);
    });
});
