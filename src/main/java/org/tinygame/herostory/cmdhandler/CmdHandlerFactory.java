package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令处理器工厂
 */
public final class CmdHandlerFactory {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    /**
     * 处理器字典
     */
    static private final Map<Class<?>, ICmdHandler<? extends GeneratedMessage>> _handlerMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private CmdHandlerFactory() {
    }

    /**
     * 初始化
     */
    static public void init() {
        LOGGER.info("==== 完成 Cmd 和 Handler 的关联 ====");

        // 获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        // 获取所有的 ICmdHandler 子类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
            packageName,
            true,
            ICmdHandler.class
        );

        for (Class<?> cmdHandlerClazz : clazzSet) {
            if (null == cmdHandlerClazz ||
                0 != (cmdHandlerClazz.getModifiers() & Modifier.ABSTRACT)) {
                // 如果是抽象类,
                continue;
            }

            // 获取方法数组
            Method[] methodArray = cmdHandlerClazz.getDeclaredMethods();
            // 命令类
            Class<?> cmdClazz = null;

            for (Method currMethod : methodArray) {
                if (!currMethod.getName().equals("handle")) {
                    // 如果不是 handle 方法,
                    continue;
                }

                // 获取函数参数类型
                Class<?>[] paramTypeArray = currMethod.getParameterTypes();

                if (paramTypeArray.length < 2 ||
                    paramTypeArray[1] == GeneratedMessage.class || // 这里最好加上这个判断
                    !GeneratedMessage.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }

                cmdClazz = paramTypeArray[1];
                break;
            }

            if (null == cmdClazz) {
                continue;
            }

            try {
                // 创建指令处理器
                ICmdHandler<?> newHandler = (ICmdHandler<?>) cmdHandlerClazz.newInstance();

                LOGGER.info(
                    "关联 {} <==> {}",
                    cmdClazz.getName(),
                    cmdHandlerClazz.getName()
                );

                _handlerMap.put(cmdClazz, newHandler);
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 创建指令处理器工厂
     *
     * @param msgClazz 消息类
     * @return 指令处理器
     */
    static public ICmdHandler<? extends GeneratedMessage> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }

        return _handlerMap.get(msgClazz);
    }
}
