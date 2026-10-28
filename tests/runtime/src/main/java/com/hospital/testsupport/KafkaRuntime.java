package com.hospital.testsupport;
import java.nio.file.*;import java.util.*;import java.util.concurrent.CountDownLatch;import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;
/** Isolated real Kafka broker for integration validation. Never deployed as an application service. */
public final class KafkaRuntime {
 public static void main(String[]args)throws Exception{
  if(args.length>1&&args[0].equals("--create-topics")){try(var admin=org.apache.kafka.clients.admin.AdminClient.create(Map.of("bootstrap.servers",args[1]))){var requested=new ArrayList<org.apache.kafka.clients.admin.NewTopic>();for(int i=2;i<args.length;i++)requested.add(new org.apache.kafka.clients.admin.NewTopic(args[i],3,(short)1));try{admin.createTopics(requested).all().get(30,java.util.concurrent.TimeUnit.SECONDS);}catch(java.util.concurrent.ExecutionException exception){if(!(exception.getCause() instanceof org.apache.kafka.common.errors.TopicExistsException))throw exception;}}return;}
  var topics=new ArrayList<String>();for(String topic:List.of("auth.security-event.v1","patient.registered.v1","patient.updated.v1","staff.created.v1","staff.leave-approved.v1","appointment.booked.v1","appointment.rescheduled.v1","appointment.cancelled.v1","medical-record.created.v1","lab.order-created.v1","lab.result-verified.v1","pharmacy.stock-expiring.v1","pharmacy.stock-low.v1","pharmacy.medicine-dispensed.v1","billing.invoice-created.v1","billing.payment-completed.v1","billing.refund-completed.v1","notification.requested.v1")){topics.add(topic);topics.add(topic+".DLT");}
  var kafka=new EmbeddedKafkaKraftBroker(1,3,topics.toArray(String[]::new));kafka.brokerProperties(Map.of("auto.create.topics.enable","false"));kafka.afterPropertiesSet();
  Path file=Path.of(System.getProperty("hospital.kafka.addressFile","kafka-address.txt"));Files.writeString(file,kafka.getBrokersAsString());System.out.println("Hospital integration Kafka ready: "+kafka.getBrokersAsString());Runtime.getRuntime().addShutdownHook(new Thread(kafka::destroy));new CountDownLatch(1).await();
 }
}
