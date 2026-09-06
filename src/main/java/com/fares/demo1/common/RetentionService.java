package com.fares.demo1.common;

import com.fares.demo1.common.RetentionProperties;
import com.fares.demo1.activity.ActivitySnapshotRepo;
import com.fares.demo1.database.DatabaseSnapshotRepo;
import com.fares.demo1.host.HostSystemSnapshotRepo;
import com.fares.demo1.event.MonitorEventRepo;
import com.fares.demo1.workload.QueryDigestSampleRepo;
import com.fares.demo1.security.SecurityFindingRepo;
import com.fares.demo1.workload.SessionSampleRepo;
import com.fares.demo1.workload.TableSizeSampleRepo;
import com.fares.demo1.workload.WaitSampleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Deletes the per-cycle rows (one-row snapshots, workload detail, and long-resolved
 * events) once they are older than {@link RetentionProperties#maxAge()}. Runs hourly so
 * the tables never drift more than an hour past the window. Config snapshots and
 * still-open events are left alone.
 *
 * <p>Deletion is time-based only for now; rolling old rows up into hourly/daily
 * averages before deleting them (to keep long-term trends cheaply) is a later step.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RetentionService {

    private final DatabaseSnapshotRepo databaseSnapshotRepo;
    private final HostSystemSnapshotRepo hostSystemSnapshotRepo;
    private final ActivitySnapshotRepo activitySnapshotRepo;
    private final MonitorEventRepo monitorEventRepo;
    private final QueryDigestSampleRepo queryDigestSampleRepo;
    private final SessionSampleRepo sessionSampleRepo;
    private final WaitSampleRepo waitSampleRepo;
    private final TableSizeSampleRepo tableSizeSampleRepo;
    private final SecurityFindingRepo securityFindingRepo;
    private final RetentionProperties props;

    @Scheduled(fixedRate = 3_600_000, initialDelay = 120_000)   // hourly, 2 min after startup
    @Transactional
    public void purge() {
        if (!props.isEnabled()) {
            return;
        }
        Instant cutoff = Instant.now().minus(props.maxAge());

        int db = databaseSnapshotRepo.deleteOlderThan(cutoff);
        int host = hostSystemSnapshotRepo.deleteOlderThan(cutoff);
        int activity = activitySnapshotRepo.deleteOlderThan(cutoff);
        int events = monitorEventRepo.deleteResolvedBefore(cutoff);
        int digests = queryDigestSampleRepo.deleteOlderThan(cutoff);
        int sessions = sessionSampleRepo.deleteOlderThan(cutoff);
        int waits = waitSampleRepo.deleteOlderThan(cutoff);
        int tables = tableSizeSampleRepo.deleteOlderThan(cutoff);
        int security = securityFindingRepo.deleteOlderThan(cutoff);

        int total = db + host + activity + events + digests + sessions + waits + tables + security;
        if (total > 0) {
            log.info("retention purge (older than {}): db={} host={} activity={} events={} "
                            + "digests={} sessions={} waits={} tables={} security={}",
                    cutoff, db, host, activity, events, digests, sessions, waits, tables, security);
        }
    }
}
