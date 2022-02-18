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
### topicの作成
1. 以下のコマンドを実施（通常はアプリで生成するので不要）
   ```
   docker-compose exec broker01 /kafka/bin/kafka-topics.sh --create --bootstrap-server broker01:9091 --topic quickstart-events --partitions 32 --replication-factor 2
   ```
1. 結果確認
   ```
   docker-compose exec broker01 /kafka/bin/kafka-topics.sh --list --bootstrap-server broker01:9091 
   ```
   出力結果
   ```
   __consumer_offsets
   quickstart-events
   ```
   詳細表示は以下のコマンド
   ```
   docker-compose exec broker01 /kafka/bin/kafka-topics.sh --describe --bootstrap-server broker01:9091 --topic quickstart-events
   ```
   出力イメージ
   ```
   Topic: quickstart-events        TopicId: yuofUzWJR2yfbMNlRfJrwg PartitionCount: 32      ReplicationFactor: 2    Configs: cleanup.policy=compact,segment.bytes=1073741824
        Topic: quickstart-events        Partition: 0    Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: quickstart-events        Partition: 1    Leader: 3       Replicas: 3,2   Isr: 3,2
        Topic: quickstart-events        Partition: 2    Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: quickstart-events        Partition: 3    Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: quickstart-events        Partition: 4    Leader: 3       Replicas: 3,1   Isr: 3,1
        Topic: quickstart-events        Partition: 5    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: quickstart-events        Partition: 6    Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: quickstart-events        Partition: 7    Leader: 3       Replicas: 3,2   Isr: 3,2
        Topic: quickstart-events        Partition: 8    Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: quickstart-events        Partition: 9    Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: quickstart-events        Partition: 10   Leader: 3       Replicas: 3,1   Isr: 3,1
        Topic: quickstart-events        Partition: 11   Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: quickstart-events        Partition: 12   Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: quickstart-events        Partition: 13   Leader: 3       Replicas: 3,2   Isr: 3,2
        Topic: quickstart-events        Partition: 14   Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: quickstart-events        Partition: 15   Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: quickstart-events        Partition: 16   Leader: 3       Replicas: 3,1   Isr: 3,1
        Topic: quickstart-events        Partition: 17   Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: quickstart-events        Partition: 18   Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: quickstart-events        Partition: 19   Leader: 3       Replicas: 3,2   Isr: 3,2
        Topic: quickstart-events        Partition: 20   Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: quickstart-events        Partition: 21   Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: quickstart-events        Partition: 22   Leader: 3       Replicas: 3,1   Isr: 3,1
        Topic: quickstart-events        Partition: 23   Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: quickstart-events        Partition: 24   Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: quickstart-events        Partition: 25   Leader: 3       Replicas: 3,2   Isr: 3,2
        Topic: quickstart-events        Partition: 26   Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: quickstart-events        Partition: 27   Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: quickstart-events        Partition: 28   Leader: 3       Replicas: 3,1   Isr: 3,1
        Topic: quickstart-events        Partition: 29   Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: quickstart-events        Partition: 30   Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: quickstart-events        Partition: 31   Leader: 3       Replicas: 3,2   Isr: 3,2
   ```

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
