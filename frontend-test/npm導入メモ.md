# 導入メモ

## 　 npm init で package.json の作成

```code
npm init  //Nodeプロジェクトとして認識させる
```

<span style="color:red;">
    ※package.jsonは
    <pre>npm install　〇〇</pre>
    を実行したときに
    プロジェクト内にpackage.jsonがない場合自動作成される.そのためチュートリアルでは省略されることが多い.
</span>

## jest で単体テストする時の条件

testMatch 条件にあるファイル名にする必要ある

```code
**/__tests__/**/*.test.js
**/?(*.)+(spec|test).js

つまり以下のような例であればなんでも良い

__tests__/sum.test.js
tests/sum.test.js
src/sum.test.js
anywhere/sum.spec.js
```

ファイル名に test.が必要
複数あるファイルのうち任意の test を実施したい場合は
`npm test --tests/ファイル指定`
jest はデフォルトでは CommonJS ベースなので
メソッドを読み込むときは
読み込ませたいファイル：`module.exports = メソッド名;`
テスト実施ファイル：`const sum = require("../〇〇");`
