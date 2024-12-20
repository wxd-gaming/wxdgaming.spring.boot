package wxdgaming.spring.boot.rpc;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.SpringReflectContent;
import wxdgaming.spring.boot.core.ann.AppStart;
import wxdgaming.spring.boot.core.ann.ReLoad;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.server.SocketService;

/**
 * rpc 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 23:25
 **/
@Slf4j
@Getter
@Setter
@Service
public class RpcService implements InitPrint {

    RpcDispatcher rpcDispatcher;

    public RpcService(@Value("${socket.rpc-token:getg6jhkopw435dvmkmcvx5y63-40}")
                      String RPC_TOKEN) {
        this.rpcDispatcher = new RpcDispatcher(RPC_TOKEN);
    }

    @AppStart
    @ReLoad
    @Order(99999)
    public void initMapping(SpringReflectContent springReflectContent) {
        rpcDispatcher.initMapping(springReflectContent);
        /*注册扫描rpc消息*/
        springReflectContent.withSuper(SocketClient.class)
                .filter(socketClient -> socketClient.getConfig().isEnableRpc())
                .forEach(socketClient -> {
                    socketClient.getClientMessageDecode().getDispatcher().initMapping(
                            springReflectContent,
                            new String[]{RpcScan.class.getPackageName()}
                    );
                });

        /*注册扫描rpc消息*/
        springReflectContent.withSuper(SocketService.class)
                .filter(socketService -> socketService.getConfig().isEnableRpc())
                .forEach(socketService -> {
                    socketService.getServerMessageDecode().getDispatcher().initMapping(
                            springReflectContent,
                            new String[]{RpcScan.class.getPackageName()}
                    );
                });

    }


}
