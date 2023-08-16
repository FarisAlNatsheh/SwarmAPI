package swarmAPIAdmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import requestBodies.ActionRequest;
import requestBodies.Robot;

@RestController
public class MainAPI {
	private int x = 0;
	public static HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	public static HashMap<String, String> ESPs = new HashMap<String, String>();
	public static ArrayList<ActionRequest> actions = new ArrayList<ActionRequest>();
	public static HashMap<String, Queue<ActionRequest>> actionQueue = new HashMap<String, Queue<ActionRequest>>();

	@PostMapping("/esp")
	public ResponseEntity<String> espRegister(@RequestParam String key, @RequestParam String name) {
		ESPs.put(name, key);
		actionQueue.put(key, new LinkedList<ActionRequest>());
		return ResponseEntity.ok("Test session created");
	}
	//Send an action request to another robot (any message here can be read by the other robot)
	@PostMapping("/queue")
	public ResponseEntity<String> queueOrder(@RequestParam String senderKey, @RequestParam String receiverName,@RequestBody ActionRequest action) {

		try {
			action.setFromID(senderKey);
			action.setToID(ESPs.get(receiverName));
			actionQueue.get(ESPs.get(receiverName)).add(action);
		}
		catch(Exception e) {
			ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Action failed to send");
		}
		return ResponseEntity.ok("Action sent");

	}

	@GetMapping("/active-esp")
	public ArrayList<String> getActiveESP() {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> entry : ESPs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			list.add(key + ": " + value);
		}

		return list;
	}

	@GetMapping("/active-queues")
	public ArrayList<String> getQueues() {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Queue<ActionRequest>> entry : actionQueue.entrySet()) {
			String key = entry.getKey();
			Queue<ActionRequest> value = entry.getValue();
			list.add(key + ": " + value);
		}

		return list;
	}

	@PostMapping("/dequeue")
	public String dequeue(@RequestParam String key) {
		if(actionQueue.containsKey(key))
			if(!actionQueue.get(key).isEmpty())
				return actionQueue.get(key).remove().getPayload();

		return "No actions queued";
	}



}
