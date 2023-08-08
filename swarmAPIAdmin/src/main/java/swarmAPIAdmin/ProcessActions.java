package swarmAPIAdmin;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import requestBodies.ActionRequest;

@Service
public class ProcessActions {
	@Scheduled(fixedDelayString = "#{${process-speed}}")
	public void processOrder() {
		ActionRequest request = MainAPI.actions.get(0);
		//Send request to other API then remove actions.
		//We might use this class if we have multiple APIs
	}
}
