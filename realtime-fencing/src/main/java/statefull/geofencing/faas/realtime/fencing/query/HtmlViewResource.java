package statefull.geofencing.faas.realtime.fencing.query;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;


@Controller
public class HtmlViewResource {

    public static final ZonedDateTime EPOCH = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
    private FenceViewResource fenceView;

    public HtmlViewResource(FenceViewResource fenceView) {
        this.fenceView = fenceView;
    }

//    @GetMapping(value = "/api/views/fences", produces = MediaType.TEXT_HTML_VALUE)
//    public String getFences(Model model, @RequestParam(value = "sinceHoursAgo", required = false) Optional<Integer> hours) {
//        var pickUpTimeThreshold = hours.map(ZonedDateTime.now()::minusHours).orElse(EPOCH);
//        var fences = fenceView.getFences(true)
//                .flatMapIterable(FenceStatusesDto::getFences)
//                .filter(fence -> getPickUpTime(fence).isAfter(pickUpTimeThreshold))
//                .sort(this::fenceSorter);
//        var variable = new ReactiveDataDriverContextVariable(fences, 1000);
//        model.addAttribute("fences", variable);
//        return "fences";
//    }
//
//    @GetMapping(value = "/api/views/orders", produces = MediaType.TEXT_HTML_VALUE)
//    public String getOrders(Model model) {
//        var variable = new ReactiveDataDriverContextVariable(
//                orderView.getOrders(true).flatMapIterable(OrderStatusesDto::getOrders), 1000);
//        model.addAttribute("orders", variable);
//        return "orders";
//    }
//
//    private int fenceSorter(FenceStatusDto fence1, FenceStatusDto fence2) {
//        var time1 = getPickUpTime(fence1);
//        var time2 = getPickUpTime(fence2);
//        return time2.compareTo(time1);
//    }
//
//    private ZonedDateTime getPickUpTime(FenceStatusDto fence) {
//        return fence.getTasks().stream()
//                .filter(taskDto -> taskDto.getType()== PICKUP)
//                .findFirst()
//                .map(TaskDto::getTime)
//                .orElse(EPOCH);
//    }
}
