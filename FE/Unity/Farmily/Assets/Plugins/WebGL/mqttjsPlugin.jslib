var mqttjsPlugin = {

  $DataMqttJs: {
    out_msg: "",
    topic: "",
  },

  mqttConnect: function (brokerAddress, topicSub) {
    console.log("Connecting to broker");
    var client = mqtt.connect(UTF8ToString(brokerAddress));
    var topic = UTF8ToString(topicSub);
    console.log(topic);

    client.on("connect", function () {
      console.log("Connected");
      client.subscribe(topic);
      client.on("message", function (incomingTopic, message) {
        console.log("Received message:", incomingTopic, message.toString());
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
