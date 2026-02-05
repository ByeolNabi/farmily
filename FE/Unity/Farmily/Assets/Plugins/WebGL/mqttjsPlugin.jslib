var mqttjsPlugin = {

  $DataMqttJs: {
    out_msg: "",
    topic: "",
  },

  mqttConnect: function (brokerAddress, topicSubCsv) {
    console.log("Connecting to broker");
    var client = mqtt.connect(UTF8ToString(brokerAddress));
    
    var topics = UTF8ToString(topicSubCsv)
      .split(",")
      .map(function (t) { return t.trim();})
      .filter(function (t) { return t.length > 0;});

    client.on("connect", function () {
      console.log("Connected");
      if(topics.length > 0){
        client.subscribe(topics);
      }
      client.on("message", function (incomingTopic, message) {
        SendMessage("mqttResponse", "GetData", message.toString());
      });
    });

    client.on("error", function (error) {
      console.log(error);
    });
  },
};

autoAddDeps(mqttjsPlugin, "$DataMqttJs");
mergeInto(LibraryManager.library, mqttjsPlugin);
