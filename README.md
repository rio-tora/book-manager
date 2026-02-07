## 目的
課題要件である以下を実装:
- 書籍と著者の登録・更新
- 著者に紐づく本の取得

## 実行手順
docker compose up -d → gradlew generateJooq → gradlew bootRun

## 実装内容
- Author API
  - POST /authors
  - GET /authors/{id}
  - PATCH /authors/{id}
- Book API
  - POST /books
  - PATCH /books/{id}
- Relation API
  - GET /authors/{id}/books
- 例外ハンドリング
  - ResourceNotFoundException
  - BusinessRuleViolationException
  - GlobalExceptionHandler
- バリデーション
  - DTOの入力チェック
  - 存在しないauthorIds検証

## 動作確認
- Author作成/取得/更新: OK
- Book作成/更新: OK
- GET /authors/{id}/books: OK
- 異常系
  - 不正入力: 400
  - 未存在ID: 404

## 補足
- Flyway + jOOQ生成パイプラインを再構築済み
- jOOQ生成物は `build/generated-src/jooq/main`
