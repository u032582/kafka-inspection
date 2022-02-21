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

### 環境削除
1. docker-compose 削除
   ```
   docker-compose down
   ```
## Appendix
### 検証したソフトウェアバージョン
- Kafka ブローカー：2.13-2.8.1
- spring kafka：2.8.2
- kafka-client：3.0.0

### 検証で変更した server.properties
```
# コンシューマのオフセットを保存するトピックのレプリカ数
offsets.topic.replication.factor=2

# トランザクションを保存するトピックのレプリカ数
transaction.state.log.replication.factor=2

# トランザクションを保存するトピックの最小InSyncReplica数。
# この値以下にISRが小さくなるとProducerからの送信がブロックされる
# たとえば設定値が1の場合、ISRが1になると送信できない。
transaction.state.log.min.isr=1

# トランザクションのタイムアウト時間
transaction.timeout.ms=600000

# Topicを削除可能とするか
delete.topic.enable=true

# デフォルトのinsync.replicas最少数
# コンシューマのオフセットを保存するトピックにもこの値が効く
min.insync.replicas=2

# ブローカーのID。ブローカーごとにユニークにする必要がある
broker.id=1

# zookeeprに登録するブローカのエンドポイント。クライアントから解決可能なアドレスである必要がある。
listeners=PLAINTEXT://192.168.100.168:9092

# デフォルトのトピックパーティション数。ブローカ台数に合わせてお好みで。
num.partitions=32

# zookeeperのエンドポイント。ブローカーから解決可能なアドレスである必要がある。
zookeeper.connect=192.168.100.174:2181
```

### spring kafka のspring bootプロパティ一覧
- https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.integration

### ブローカの追加とパーティションのリバランス
ブローカーの追加ではTopicのパーティションリバランスは発生しない。以下の手順で明示的にリバランスする必要がある。

1. 以下のファイルを作成する。ファイル名は`topic-move.json`とした。リバランスしたいトピック名（以下では`quickstart-events`）をtopicsに羅列する。  
   ```
   {"topics":
      [{"topic": "quickstart-events"}],
      "version":1
   }
   ```
1. 以下のコマンドを実行し、パーティションのバランス設定を生成する。--broker-listに配置したいbroker のidをリストする。
   ```
   kafka/bin/kafka-reassign-partitions.sh --topics-to-move-json-file topic-move.json --broker-list 1,2,3,4 --bootstrap-server 192.168.100.168:9092 --generate 
   ```
   以下のような出力となるので、`Proposed partition reassignment configuration` 以下のJSONを ファイルに保存する。今回は`new_parition.json`とした。
   ```
   Current partition replica assignment
   {"version":1,"partitions":[{"topic":"quickstart-events","partition":0,"replicas":[3,2,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":1,"replicas":[4,3,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":2,"replicas":[1,4,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":3,"replicas":[2,1,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":4,"replicas":[3,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":5,"replicas":[4,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":6,"replicas":[1,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":7,"replicas":[2,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":8,"replicas":[3,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":9,"replicas":[4,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":10,"replicas":[1,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":11,"replicas":[2,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":12,"replicas":[3,2,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":13,"replicas":[4,3,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":14,"replicas":[1,4,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":15,"replicas":[2,1,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":16,"replicas":[3,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":17,"replicas":[4,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":18,"replicas":[1,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":19,"replicas":[2,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":20,"replicas":[3,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":21,"replicas":[4,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":22,"replicas":[1,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":23,"replicas":[2,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":24,"replicas":[3,2,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":25,"replicas":[4,3,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":26,"replicas":[1,4,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":27,"replicas":[2,1,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":28,"replicas":[3,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":29,"replicas":[4,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":30,"replicas":[1,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":31,"replicas":[2,3,4],"log_dirs":["any","any","any"]}]}

   Proposed partition reassignment configuration
   {"version":1,"partitions":[{"topic":"quickstart-events","partition":0,"replicas":[2,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":1,"replicas":[3,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":2,"replicas":[4,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":3,"replicas":[1,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":4,"replicas":[2,1,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":5,"replicas":[3,2,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":6,"replicas":[4,3,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":7,"replicas":[1,4,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":8,"replicas":[2,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":9,"replicas":[3,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":10,"replicas":[4,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":11,"replicas":[1,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":12,"replicas":[2,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":13,"replicas":[3,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":14,"replicas":[4,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":15,"replicas":[1,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":16,"replicas":[2,1,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":17,"replicas":[3,2,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":18,"replicas":[4,3,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":19,"replicas":[1,4,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":20,"replicas":[2,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":21,"replicas":[3,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":22,"replicas":[4,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":23,"replicas":[1,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":24,"replicas":[2,4,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":25,"replicas":[3,1,2],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":26,"replicas":[4,2,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":27,"replicas":[1,3,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":28,"replicas":[2,1,3],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":29,"replicas":[3,2,4],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":30,"replicas":[4,3,1],"log_dirs":["any","any","any"]},{"topic":"quickstart-events","partition":31,"replicas":[1,4,2],"log_dirs":["any","any","any"]}]}
   ```
1. 以下のコマンドを実行し、パーティションのリバランスを実施する。
   ```
   kafka/bin/kafka-reassign-partitions.sh --reassignment-json-file new_parition.json --bootstrap-server 192.168.100.168:9092 --execute
   ```
