package com.switchvov.magicconfig.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * distributed locks.
 *
 * @author switch
 * @since 2024/5/14
 */
@Slf4j
@Component()
public class DistributedLocks {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Getter
    private final AtomicBoolean locked = new AtomicBoolean(false);

    private Connection connection;
    private final DataSource dataSource;

    public DistributedLocks(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        executor.scheduleWithFixedDelay(this::tryLock, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    private void tryLock() {
        try {
            locked.set(lock());
        } catch (Exception e) {
            log.debug(" ===>[MagicConfig] lock failed...]");
            locked.set(false);
        }
    }

    private boolean lock() throws SQLException {
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        connection.createStatement().execute("SET innodb_lock_wait_timeout=5");
        // lock 5s
        connection.createStatement().execute("SELECT app FROM locks WHERE id=1 FOR UPDATE");
        if (locked.get()) {
            log.debug(" ===>[MagicConfig] reenter this dist lock.");
        } else {
            log.debug(" ===>[MagicConfig] get a dist lock.");
        }
        return true;
    }

    @PreDestroy
    public void close() {
        try {
            if (Objects.nonNull(connection) && !connection.isClosed()) {
                connection.rollback();
                connection.close();
            }
        } catch (Exception e) {
            log.warn(" ===>[MagicConfig] ignore this close exception");
        }
    }
}
