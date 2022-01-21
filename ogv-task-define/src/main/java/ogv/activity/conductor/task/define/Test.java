package ogv.activity.conductor.task.define;

import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.spring.ClientProperties;
import com.netflix.conductor.common.metadata.tasks.Task;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class Test {

    @Resource
    private ClientProperties clientProperties;

    @Resource
    private TaskClient taskClient;

    @Resource
    private TaskRunnerConfigurer taskRunnerConfigurer;

    @PostConstruct
    public void init() throws Exception{
        System.out.println(1);
        Task task = taskClient.pollTask("task_1", "1", null);

        if (task.getTaskId() != null){
            boolean success = taskClient.ack(task.getTaskId(), "1");
            System.out.println(success);
        }
        System.out.println(1);
    }

}
