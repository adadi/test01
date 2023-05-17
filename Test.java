import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
public class CaseServiceSteps {

    @Autowired
    private ICaseService caseService;

    private LocalDate startBookingDate;
    private LocalDate endBookingDate;
    private Set<Integer> caseIds;
    private Mono<List<CaseExternalDto>> casesResult;
    private Mono<LastUpdateDto> lastUpdateResult;

    @Given("I have the start booking date {string}")
    public void iHaveTheStartBookingDate(String startBookingDate) {
        this.startBookingDate = LocalDate.parse(startBookingDate);
    }

    @Given("I have the end booking date {string}")
    public void iHaveTheEndBookingDate(String endBookingDate) {
        this.endBookingDate = LocalDate.parse(endBookingDate);
    }

    @Given("I have the following case IDs:")
    public void iHaveTheFollowingCaseIDs(List<Integer> caseIds) {
        this.caseIds = new HashSet<>(caseIds);
    }

    @When("I call the getCases method")
    public void iCallTheGetCasesMethod() {
        casesResult = caseService.getCases(startBookingDate, endBookingDate);
    }

    @When("I call the getCaseByIds method")
    public void iCallTheGetCaseByIdsMethod() {
        casesResult = caseService.getCaseByIds(caseIds);
    }

    @When("I call the getLastUpdate method")
    public void iCallTheGetLastUpdateMethod() {
        lastUpdateResult = caseService.getLastUpdate();
    }

    @Then("I should receive a list of CaseExternalDto")
    public void iShouldReceiveAListOfCaseExternalDto() {
        assertNotNull(casesResult);
        casesResult.subscribe(caseExternalDtos -> assertFalse(caseExternalDtos.isEmpty()));
    }

    @Then("I should receive a LastUpdateDto")
    public void iShouldReceiveALastUpdateDto() {
        assertNotNull(lastUpdateResult);
    }
}
