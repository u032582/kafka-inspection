# KAFKA検証コード

## 事前準備
- dockerおよびdocker-compose が利用できること
- jdk17 が利用できること

## 環境構築
1. リポジトリのクローン
   ``` 
   git clone https://github.com/u032582/kafka-inspection.git
   ``` 
1. 検証コードのイメージをビルドする  
   ``` 
   cd kafka-inspection
   ./gradlew bootBuildImage
   ```

 1. dockerネットワークを作成  
    ```
    docker create network sandbox-net
    ```

1. docker-compose起動
   ```
   docker-compose up -d
   ```

## 利用方法
### producer
1. ブラウザで以下のURLにアクセスする。
   ```
   http://localhost:8080/swagger.html
   ```
   GET http://localhost:8080/sendrequest を呼び出します。 
   - testName：テストの試行ごとにつける名前です
   - loopNum：メッセージを送信する回数です
   - intervalMs：メッセージの送信間隔（ms）です
   - exceptionOccur：メッセージ送信後に意図的に例外を発生させます。トランザクションロールバックの試験に用います。
### zookeeper
1. ブラウザで以下のURLにアクセスする。
   ```
   http://localhost:30001/
   ```
1. connection string に以下を指定して Connectする
   ```
   zookeeper
   ```


### 試験結果
1. 送信および受信結果はpostgresql内に保存されている。ブラウザで以下のURLにアクセスして参照できる。
   ```
   http://localhost:30002/?pgsql=postgres&username=postgres&db=postgres&ns=public
   ```
   - データベース名：postgres
   - ユーザー：postgres
   - パスワード：password
   - リクエストテーブル名：request
   - 試験結果テーブル名：result

1. 検証コードのログ確認
   ```
   docker-compose logs -f comsumer01
   ```
   ```
   docker-compose logs -f comsumer02
   ```


## 環境削除
1. docker-compose起動
   ```
   docker-compose down
   ```
