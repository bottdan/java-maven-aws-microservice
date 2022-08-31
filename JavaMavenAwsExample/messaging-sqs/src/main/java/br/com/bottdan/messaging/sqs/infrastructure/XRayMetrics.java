package br.com.bottdan.messaging.sqs.infrastructure;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.TraceHeader;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class XRayMetrics {

    private XRayMetrics() {
        super();
    }

    public static void trace(final String segmentName, final Map<String, Object> headers, final XRayMetricsTrace runnable) {

    }

    @FunctionalInterface
    public interface XRayMetricsTrace {
        Set<String> XRAY_HEADERS = Set.of(TraceHeader.HEADER_KEY.toLowerCase(), "AWSTraceHeader".toLowerCase());

        default void beforeCompletion(final String segmentName) {
            if (AWSXRay.getCurrentSegmentOptional().isEmpty()) {
                AWSXRay.beginSegment(segmentName);
            }
        }

        void run() throws Throwable;

        default void recoverTrace(final Map<String, Object> headers) {
            final Optional<Map.Entry<String, Object>> optional = headers.entrySet().stream().filter(filterHeaders()).findFirst();

            final String traceHeaderValue = optional.isPresent() ? optional.get().getValue().toString() : null;

            if (StringUtils.isNoneBlank(traceHeaderValue)) {
                final TraceHeader traceHeader = TraceHeader.fromString(traceHeaderValue);
                final Segment segment = AWSXRay.getCurrentSegment();
                segment.setTraceId(traceHeader.getRootTraceId());
                segment.setParentId(traceHeader.getParentId());
                segment.setSampled(TraceHeader.SampleDecision.SAMPLED.equals(traceHeader.getSampled()));
            }
        }

        default Predicate<Map.Entry<String, Object>> filterHeaders() {
            return entry -> StringUtils.isNoneBlank(entry.getKey()) && XRAY_HEADERS.contains(entry.getKey().toLowerCase());
        }
    }
}
