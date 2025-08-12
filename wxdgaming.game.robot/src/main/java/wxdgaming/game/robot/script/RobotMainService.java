package wxdgaming.game.robot.script;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.game.message.chat.ChatType;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.message.role.ReqHeartbeat;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.game.message.task.ReqAcceptTask;
import wxdgaming.game.message.task.ReqSubmitTask;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.robot.RobotBootstrapConfig;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.httpclient.HttpRequestPost;
import wxdgaming.spring.boot.net.httpclient.HttpResponse;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

import java.util.concurrent.ConcurrentHashMap;

/**
 * socket
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 13:20
 **/
@Slf4j
@Getter
@Service
public class RobotMainService {

    final RobotBootstrapConfig robotBootstrapConfig;
    final SocketClient socketClient;
    final ConcurrentHashMap<String, Robot> robotMap = new ConcurrentHashMap<>();

    public RobotMainService(RobotBootstrapConfig robotBootstrapConfig, SocketClient socketClient) {
        this.robotBootstrapConfig = robotBootstrapConfig;
        this.socketClient = socketClient;
    }

    @Start
    public void start() {
        for (int i = 0; i < 1; i++) {
            String account = "b4" + (i + 1);
            robotMap.put(account, new Robot().setAccount(account).setName(account));
        }
    }

    public RunResult httpLogin(Robot robot) {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("appId", 1);
        jsonObject.put("platform", 1);
        jsonObject.put("account", robot.getAccount());
        jsonObject.put("token", robot.getAccount());

        String uriPath = robotBootstrapConfig.getLoginUrl() + "/login/check";
        HttpResponse httpResponse = HttpRequestPost.of(uriPath, jsonObject).execute();
        RunResult runResult = httpResponse.bodyRunResult();
        if (runResult.isFail()) {
            log.error("登录失败：{}", runResult.msg());
        }
        return runResult;
    }

    /** 1秒一次主循环 */
    @Scheduled("*/5")
    public void timer() {
        for (Robot robot : robotMap.values()) {
            SocketSession socketSession = robot.getSocketSession();
            if (socketSession == null || !socketSession.isOpen()) {
                RunResult runResult = httpLogin(robot);
                if (runResult.isOk()) {
                    String platformUserId = runResult.getString("userId");
                    String host = runResult.getString("host");
                    if (StringUtils.isBlank(host)) {
                        log.info("无可用的网关。。。。。");
                        return;
                    }
                    int port = runResult.getIntValue("port");
                    log.info("登录成功：{}, 网关地址={}:{}", robot, host, port);
                    socketClient.connect(host, port, connect -> {
                        robot.setSocketSession(connect);
                        connect.getChannel().closeFuture().addListener(new ChannelFutureListener() {
                            @Override public void operationComplete(ChannelFuture future) throws Exception {
                                robot.setSendLogin(false);
                                robot.setLoginEnd(false);
                                robot.setSocketSession(null);
                            }
                        });
                        connect.bindData("robot", robot);

                        robot.setSendLogin(true);
                        connect.write(
                                new ReqLogin()
                                        .setAccount(robot.getAccount())
                                        .setSid(1)
                                        .setToken(runResult.getString("token"))
                        );

                    });
                }
            } else {
                ReqHeartbeat reqHeartbeat = new ReqHeartbeat();
                socketSession.write(reqHeartbeat);
            }
        }
    }

    /** 1秒一次主循环 */
    @Scheduled("*/30")
    public void timer30() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd() && robot.getLevel() > 10) {
                if (!RandomUtils.randomBoolean()) {
                    continue;
                }
                ReqChatMessage reqChatMessage = new ReqChatMessage();
                reqChatMessage.setType(ChatType.Chat_TYPE_World);
                reqChatMessage.setContent("你好");
                robot.getSocketSession().write(reqChatMessage);
            }
        }
    }

    /** 1秒一次主循环 */
    @Scheduled("*/5")
    public void timerTask() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd()) {
                if (!RandomUtils.randomBoolean()) {
                    continue;
                }
                for (TaskBean taskBean : robot.getTasks().values()) {
                    if (!taskBean.isAccept()) {
                        ReqAcceptTask reqAcceptTask = new ReqAcceptTask();
                        reqAcceptTask.setTaskType(TaskType.Main);
                        reqAcceptTask.setTaskId(taskBean.getTaskId());
                        robot.getSocketSession().write(reqAcceptTask);
                    } else if (!taskBean.isCompleted()) {
                        ReqChatMessage reqChatMessage = new ReqChatMessage();
                        reqChatMessage.setType(ChatType.Chat_TYPE_World);
                        reqChatMessage.setContent("@gm completeTask " + taskBean.getTaskId());
                        robot.getSocketSession().write(reqChatMessage);
                    } else if (!taskBean.isReward()) {
                        ReqSubmitTask reqSubmitTask = new ReqSubmitTask();
                        reqSubmitTask.setTaskType(TaskType.Main);
                        reqSubmitTask.setTaskId(taskBean.getTaskId());
                        robot.getSocketSession().write(reqSubmitTask);
                    }
                }
            }
        }
    }

    /** 1秒一次主循环 */
    @Scheduled(value = "*/5", async = true)
    public void timer60() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd()) {
                if (!RandomUtils.randomBoolean()) {
                    continue;
                }
                ReqChatMessage reqChatMessage = new ReqChatMessage();
                reqChatMessage.setType(ChatType.Chat_TYPE_World);
                reqChatMessage.setContent("@gm addexp " + RandomUtils.random(20, 100));
                robot.getSocketSession().write(reqChatMessage);
            }
        }
    }

}
