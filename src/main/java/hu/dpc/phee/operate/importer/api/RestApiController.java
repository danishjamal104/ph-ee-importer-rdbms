package hu.dpc.phee.operate.importer.api;

import hu.dpc.phee.operate.importer.audit.Task;
import hu.dpc.phee.operate.importer.audit.TaskRepository;
import hu.dpc.phee.operate.importer.audit.BusinessKey;
import hu.dpc.phee.operate.importer.audit.BusinessKeyRepository;
import hu.dpc.phee.operate.importer.audit.Variable;
import hu.dpc.phee.operate.importer.audit.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RestApiController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BusinessKeyRepository businessKeyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private VariableRepository variableRepository;

    @GetMapping("/variables")
    public List<List<Variable>> variables(
            @RequestParam(value = "businessKey") String businessKey,
            @RequestParam(value = "businessKeyType") String businessKeyType
    ) {
        return loadTransactions(businessKey, businessKeyType).stream()
                .map(transaction -> variableRepository.findByWorkflowInstanceKey(transaction.getWorkflowInstanceKey()))
                .collect(Collectors.toList());
    }

    @GetMapping("/tasks")
    public List<List<Task>> tasks(
            @RequestParam(value = "businessKey") String businessKey,
            @RequestParam(value = "businessKeyType") String businessKeyType
    ) {
        return loadTransactions(businessKey, businessKeyType).stream()
                .map(transaction -> taskRepository.findByWorkflowInstanceKey(transaction.getWorkflowInstanceKey()))
                .collect(Collectors.toList());
    }

    private List<BusinessKey> loadTransactions(@RequestParam("businessKey") String businessKey, @RequestParam("businessKeyType") String businessKeyType) {
        List<BusinessKey> businessKeys = businessKeyRepository.findByBusinessKeyAndBusinessKeyType(businessKey, businessKeyType);
        logger.debug("loaded {} transaction(s) for business key {} of type {}", businessKeys.size(), businessKey, businessKeyType);
        return businessKeys;
    }

}