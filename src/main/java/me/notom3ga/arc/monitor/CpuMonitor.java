package me.notom3ga.arc.monitor;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class CpuMonitor {
    private static final String OPERATING_SYSTEM_BEAN = "java.lang:type=OperatingSystem";
    private static final OperatingSystemMXBean BEAN;

    static {
        try {
            MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName diagnosticBeanName = ObjectName.getInstance(OPERATING_SYSTEM_BEAN);
            BEAN = JMX.newMXBeanProxy(beanServer, diagnosticBeanName, OperatingSystemMXBean.class);
        } catch (Exception e) {
            throw new UnsupportedOperationException("OperatingSystemMXBean is not supported by the system", e);
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static void ensureInitialization() {
    }

    public static OperatingSystemMXBean getBean() {
        return BEAN;
    }

    public interface OperatingSystemMXBean {
        double getSystemCpuLoad();
        double getProcessCpuLoad();
    }
}
