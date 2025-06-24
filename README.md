# PDF Word Test Generator Backend

これは、PDFベースの単語テストを生成し、ウェブページから単語データをスクレイピングし、単語帳を管理するために設計されたSpring Bootアプリケーションです。これらの機能のためのRESTful APIを提供します。

## 目次

- [機能](#機能)
- [使用技術](#使用技術)
- [前提条件](#前提条件)
- [セットアップ手順](#セットアップ手順)
- [APIエンドポイント](#apiエンドポイント)
- [使用方法](#使用方法)

## 機能

-   **PDF生成**: ユーザーの入力（例：テスト問題）に基づいてPDFドキュメントを生成します。
-   **ウェブスクレイピング**: 指定されたウェブページから単語データ（単語番号、単語、意味）をスクレイピングし、MySQLデータベースに保存します。
-   **単語帳管理**: 利用可能な単語帳のリストを取得します。
-   **テスト作成**: 選択した単語帳、セクション、および単語数に基づいて、カスタマイズされた単語テストを生成します。
-   **APIドキュメント**: OpenAPIを使用して自動生成されたAPIドキュメント。

## 使用技術

-   Java 17
-   Spring Boot 3.3.4
-   Spring Data JPA
-   MySQL Database
-   Gradle
-   Apache PDFBox (PDF生成用)
-   JSoup (ウェブスクレイピング用)
-   SpringDoc OpenAPI UI (APIドキュメント用)
-   Thymeleaf
-   Docker
-   Docker Compose

## 前提条件

-   Java Development Kit (JDK) 17 (Dockerを使用しない場合)
-   Gradle (通常、Spring Bootプロジェクトには`gradlew`がバンドルされています)
-   MySQLサーバー (Dockerを使用しない場合)
-   DockerとDocker Compose (Dockerを使用する場合)

## セットアップ手順

### Dockerを使用したセットアップ

DockerとDocker Composeがインストールされていることを確認してください。

1.  **リポジトリのクローン**:
    ```bash
    git clone https://github.com/your-username/word-test-generator-be.git # 適宜リポジトリのURLを変更してください
    cd word-test-generator-be
    ```

2.  **Docker Composeでビルドと実行**:
    ```bash
    docker-compose up --build
    ```
    これにより、`db`サービス（MySQL）と`app`サービス（Spring Bootアプリケーション）がビルドおよび起動します。アプリケーションはポート`8080`で、MySQLはポート`13306`で利用可能になります。

    *   データベース接続は`docker-compose.yaml`で設定されています。
        ```yaml
        environment:
          SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/word_db
          SPRING_DATASOURCE_USERNAME: root
          SPRING_DATASOURCE_PASSWORD: rootpassword
        ```

### ローカルでのセットアップ (Dockerを使用しない場合)

1.  **リポジトリのクローン**:
    ```bash
    git clone https://github.com/your-username/word-test-generator-be.git # 適宜リポジトリのURLを変更してください
    cd word-test-generator-be
    ```

2.  **データベースのセットアップ**:
    *   MySQLサーバーが実行中であることを確認してください。
    *   `word_db`という名前のデータベースを作成します。
        ```sql
        CREATE DATABASE word_db;
        ```
    *   アプリケーションは起動時にスキーマを自動的に更新します (`spring.jpa.hibernate.ddl-auto=update`)。

3.  **設定**:
    *   アプリケーションは`src/main/resources/application.properties`を使用します。
    *   デフォルトのデータベース接続詳細は以下の通りです:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/word_db
        spring.datasource.username=root
        spring.datasource.password=ybhSvc726hdv
        ```
    *   MySQLの設定が異なる場合は、これらの値を適宜更新してください。

4.  **ビルドと実行**:
    *   Gradleを使用してプロジェクトをビルドします:
        ```bash
        ./gradlew build
        ```
    *   アプリケーションを実行します:
        ```bash
        ./gradlew bootRun
        ```
    *   アプリケーションはデフォルトで`http://localhost:8080`で起動します。

## APIエンドポイント

アプリケーション起動後、APIドキュメントは`http://localhost:8080/swagger-ui.html`でアクセスできます。

主なエンドポイント:

-   `GET /health-check`: アプリケーションのヘルスチェックを行います。
-   `POST /generate-pdf`: 提供されたテキストからPDFを生成します。
-   `GET /scrape`: 指定されたURLから単語データをスクレイピングし、データベースに保存します。
    *   パラメーター: `url`, `wordNumHeader`, `wordHeader`, `wordMeaningHeader`。
-   `GET /word-books`: 利用可能な単語帳のリストを取得します。
-   `GET /test-options`: 特定の単語帳のセクションと単語数を取得します。
    *   パラメーター: `wordBook`。
-   `POST /create-test`: カスタマイズされた単語テストを生成します。
    *   リクエストボディ: `word_book`, `sections`, `word_num`。

## 使用方法

1.  「ビルドと実行」で説明されている通りにアプリケーションを起動します。
2.  `/scrape`エンドポイントを使用して、ウェブページから単語データをデータベースに投入します。
3.  `/word-books`および`/test-options`エンドポイントを使用して、利用可能な単語データを探索します。
4.  `/create-test`エンドポイントを使用して、カスタムテストを生成します。
5.  必要に応じて、`/generate-pdf`エンドポイントを使用してPDFを生成します。