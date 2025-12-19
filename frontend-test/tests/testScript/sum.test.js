const sum = require("../script/sum.js");

test("test sample 和の計算", () => {
    expect(sum(1, 2)).toBe(3);
});
