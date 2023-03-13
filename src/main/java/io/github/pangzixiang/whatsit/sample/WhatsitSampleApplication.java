package io.github.pangzixiang.whatsit.sample;

import io.github.pangzixiang.whatsit.vertx.core.ApplicationRunner;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WhatsitSampleApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext();
        ApplicationRunner.run(applicationContext);
    }
}
