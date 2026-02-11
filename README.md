# Book Management API (書籍管理システム)

## 概要
Spring Boot と Kotlin を用いて構築された、RESTfulな書籍管理APIです。
書籍と著者の登録・更新・検索機能を提供します。

## 技術スタック (Tech Stack)

### Backend
* **Language:** Kotlin 2.2.21
* **Framework:** Spring Boot 4.0.2
* **Database Access:** jOOQ (Type safe SQL construction)
* **Migration:** Flyway
* **Build Tool:** Gradle (Kotlin DSL)

### Infrastructure / Environment
* **Database:** PostgreSQL latest (via Docker)
* **Container:** Docker Compose

## アーキテクチャと設計思想
本プロジェクトでは、**レイヤードアーキテクチャ**を採用しています。

1.  **Controller層**: HTTPリクエストのハンドリングと入力値のバリデーションに専念。
2.  **Service層**: 「出版済みの本は未出版に戻せない」等のビジネスルールをここに集約。トランザクション境界もここで制御。
3.  **Repository層**: jOOQを用い、SQLを型安全に構築。コンパイル時のエラー検知を最大化。

### こだわったポイント
* **jOOQによる型安全なDB操作**: JPAの自動生成に頼らず、SQLの柔軟性とKotlinの型安全性を両立させました。
* **ドメインルールの徹底**: 「価格は0以上」「未来の生年月日は不可」などの制約を、DB制約だけでなくアプリケーション層でもバリデーション実装し、堅牢性を高めました。
* **Dockerによる環境構築**: `docker-compose up` コマンド一つでDB環境が立ち上がるようにし、環境差異を排除しました。
* **効率的かつ実践的なテスト設計**: 単体テストによる網羅的な境界値検証に加え、setup() で必要な関連データを事前に構築するなど、テストコードの保守性と可読性にも配慮しました。

## 環境構築と実行方法 (Setup)

### 前提条件
* JDK 17 or 21
* Docker Desktop がインストールされていること

### 起動手順
1.  リポジトリをクローン
    ```bash
    git clone https://github.com/rio-tora/book-manager.git
    cd book-manager
    ```

2.  データベースの起動
    ```bash
    docker-compose up -d
    ```

3.  アプリケーションのビルドと起動
    ```bash
    ./gradlew bootRun
    ```
    ※ 初回起動時にFlywayによるマイグレーションと、jOOQによるコード生成が実行されます。

## API仕様
- Books (書籍)
  - POST /books: 書籍の登録
  - PATCH /books/{id}: 書籍の更新 (ステータス変更等のビジネスルール適用)
- Authors (著者)
  - POST /authors: 著者の登録
  - PATCH /authors/{id}: 著者の更新
  - GET /authors/{id}: 著者の詳細取得
  - GET /authors/{id}/books: 著者が執筆した書籍一覧の取得
- エラーハンドリング
  - GlobalExceptionHandler: すべての例外をハンドリングし、統一されたJSONフォーマットでエラー詳細を返却します。
  - ResourceNotFoundException: 404 Not Found
  - BusinessRuleViolationException: 400 Bad Request
...

## テストの実行
単体テストおよび結合テストを実行します。
```bash
./gradlew test
