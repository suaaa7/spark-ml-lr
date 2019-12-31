.PHONY: dbuild
dbuild:
	docker build \
		--build-arg BASE_IMAGE_TAG="8u212-b04-jdk-stretch" \
		--build-arg SBT_VERSION="1.3.5" \
		--build-arg SCALA_VERSION="2.13.1" \
		--build-arg USER_ID=1001 \
		--build-arg GROUP_ID=1001 \
		-t hseeberger/scala-sbt \
		github.com/hseeberger/scala-sbt.git#:debian

.PHONY: dbash
dbash:
	docker run --rm -it -v `pwd`:/root hseeberger/scala-sbt bash

.PHONY: dassembly
dassembly:
	docker run --rm -it -v `pwd`:/root hseeberger/scala-sbt sbt assembly

.PHONY: upload-jar
upload-jar:
	aws s3 cp \
		./batch/target/scala-2.11/batch-assembly-*.jar \
		s3://$${BUCKET_NAME}/spark/

.PHONY: upload-csv
upload-csv:
	aws s3 cp \
		./data/train_data_v1.csv \
		s3://$${BUCKET_NAME}/spark/data/

.PHONY: add-steps
add-steps:
	aws emr add-steps --cluster-id $${CLUSTER_ID} --steps \
		Type=CUSTOM_JAR,Name=SparkMLLr,ActionOnFailure=CONTINUE,Jar=command-runner.jar,Args=[spark-submit,--class,spark.ml.lr.SparkMLLrBatch,--deploy-mode,cluster,--master,yarn,--conf,'spark.executor.extraJavaOptions=-Dconfig.resource=dev.conf',--conf,'spark.driver.extraJavaOptions=-Dconfig.resource=dev.conf',s3://$${BUCKET_NAME}/spark/batch-assembly-0.1.0-SNAPSHOT.jar]

.PHONY: checkenv
checkenv:
	@echo $${BUCKET_NAME}
	@echo $${CLUSTER_ID}
