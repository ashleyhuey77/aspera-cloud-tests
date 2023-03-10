package com;

import com.app.Decrypter;
import com.app.db.ConsoleDecoration;
import com.google.gson.GsonBuilder;
import com.utils.CredentialsType;
import com.app.config.ConfigMapper;
import com.app.config.Configuration;
import com.app.thread.ex.AsyncResult;
import com.app.thread.ex.ThreadAsyncExecutor;
import com.app.thread.pool.Task;
import com.app.thread.pool.TransferTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AsperaTestApp {
  private static Logger LOGGER = LoggerFactory.getLogger(AsperaTestApp.class);

  public static void main(String[] args) throws Exception {
    try {
      ConsoleDecoration.printSection("ASPERA CLOUD LOAD TEST APP");
      LOGGER.info("=> Initializing configs...");
      Decrypter.decryptFromDB(CredentialsType.POST_EDITOR);
      ConfigMapper mapper = new ConfigMapper(new GsonBuilder().setPrettyPrinting().create());
      Configuration config = mapper.getConfig("config.json");

      LOGGER.info(
          "=> The following values are set in config.json: {}", mapper.getMapper().toJson(config));
      LOGGER.info(
          "=> Would you like to run the aspera cloud load test suite with these configurations? (Y/N)");

      // Enter data using BufferReader
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      // Reading data using readLine
      String answer = reader.readLine();

      if (answer.equalsIgnoreCase("y")) {
        ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
        List<Task> transferTasks = new ArrayList<>();
        for (int i = 0; i < config.getThreadCount(); i++) {
          transferTasks.add(new TransferTask(executor));
        }
        for (Task task : transferTasks) {
          AsyncResult<Object> start = task.start(config.getEndpoint(), config.getWaitTime());
          Object end = task.end(start);
          LOGGER.info("=> Transfer " + end.toString() + " complete.");
        }
      } else {

      }
    } catch (Exception e) {
      throw e;
    }
  }
}
