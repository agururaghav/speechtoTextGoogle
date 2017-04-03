package com.tcs.smp;

//Imports the Google Cloud client library
import com.google.cloud.speech.spi.v1beta1.SpeechClient;
import com.google.cloud.speech.v1beta1.RecognitionAudio;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1beta1.SyncRecognizeResponse;
import com.google.protobuf.ByteString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;
import net.sourceforge.javaflacencoder.FLAC_FileEncoder;

public class SpeechToText {
public static void main(String... args) throws Exception {
 // Instantiates a client
 SpeechClient speech = SpeechClient.create();

 // The path to the audio file to transcribe
 
 FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
 File inputFile = new File("./resources/brooklyn.wav");
 File outputFile = new File("./resources/brooklyn1.flac");
 flacEncoder.encode(inputFile, outputFile);
 System.out.println("converted input file");
 String fileName = "./resources/brooklyn1.flac";

 // Reads the audio file into memory
 Path path = Paths.get(fileName);
 byte[] data = Files.readAllBytes(path);
 ByteString audioBytes = ByteString.copyFrom(data);

 // Builds the sync recognize request
 RecognitionConfig config = RecognitionConfig.newBuilder()
     .setEncoding(AudioEncoding.FLAC)
     .setSampleRate(16000)
     .build();
 RecognitionAudio audio = RecognitionAudio.newBuilder()
     .setContent(audioBytes)
     .build();
 System.out.println("Audio file size = " + audio.getSerializedSize());
 System.out.println("calling google api..");
 // Performs speech recognition on the audio file
 SyncRecognizeResponse response = speech.syncRecognize(config, audio);
 //System.out.println("response string " + response.parser().);
 //SyncRecognizeResponse response = speech.asyncRecognizeAsync(config, audio);
 System.out.println("response recieved from google api...");
 List<SpeechRecognitionResult> results = response.getResultsList();
 System.out.println("printing response...");
 System.out.println("result Size = " + results.size());
 for (SpeechRecognitionResult result: results) {
   List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
   for (SpeechRecognitionAlternative alternative: alternatives) {
     System.out.printf("Transcription: %s%n", alternative.getTranscript());
   }
 }
 speech.close();
}
}
