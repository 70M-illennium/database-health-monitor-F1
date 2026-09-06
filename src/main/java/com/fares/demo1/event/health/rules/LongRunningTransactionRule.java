package com.fares.demo1.event.health.rules;

import com.fares.demo1.event.EventType;
import com.fares.demo1.common.Severity;
import com.fares.demo1.event.health.HealthContext;
import com.fares.demo1.event.health.HealthRule;
import com.fares.demo1.event.health.RuleResult;
import org.springframework.stereotype.Component;

/** Threshold: the oldest open transaction on the target has been running too long. */
@Component
public class LongRunningTransactionRule implements HealthRule {

    @Override
    public EventType type() {
        return EventType.LONG_RUNNING_TRANSACTION;
    }

    @Override
    public RuleResult evaluate(HealthContext ctx) {
        if (ctx.db().isEmpty()) {
            return RuleResult.none();
        }
        long limit = ctx.props().getLongTransactionSeconds();
        long latest = ctx.db().get(0).getOldestTransactionAgeSeconds();
        return RuleResult.of(
                ctx.db().stream().map(s -> s.getOldestTransactionAgeSeconds() >= limit).toList(),
                Severity.WARNING,
                String.format("oldest open transaction %ds (limit %ds)", latest, limit),
                (double) latest);
    }
}
