# Book Manager API

Kotlin + Spring Boot + jOOQ + Flyway + PostgreSQL で実装した、書籍・著者管理 API です。  
（コーディングテスト提出用）

---

## 技術スタック

- Kotlin
- Spring Boot
- jOOQ
- Flyway
- PostgreSQL
- Gradle (KTS)
- Java 21

---

## 機能

- 著者の登録 / 取得 / 更新
- 書籍の登録 / 更新
- 著者に紐づく書籍一覧の取得

---

## プロジェクト構成（要点）

- `api` : Controller / Request・Response DTO
- `service` : 業務ロジック
- `domain` : ドメインモデル / Repository IF
- `domain/repository` : jOOQ 実装
- `db/migration` : Flyway マイグレーション

---

## セットアップ手順（PowerShell）

### 1. PostgreSQL を起動

```powershell
docker compose up -d
