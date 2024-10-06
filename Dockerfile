# ベースイメージにOpenJDKを使用
FROM openjdk:17-jdk-slim

WORKDIR /app

# JARファイルをコンテナにコピー
COPY ./build/libs/PDFapplication-0.0.1-SNAPSHOT.jar /app/myapp.jar

EXPOSE 8080

# アプリケーションを実行
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]