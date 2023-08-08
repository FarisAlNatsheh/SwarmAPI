package swarmAPIAdmin;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import requestBodies.ActionRequest;
import requestBodies.Robot;

@RestController
public class MainAPI {
	private int x = 0;
	public static HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	public static ArrayList<ActionRequest> actions = new ArrayList<ActionRequest>();

	//Registers computer to the API
	@PostMapping("/register")
	public ResponseEntity<String> setSessionValue(HttpSession session, @RequestBody Robot r) {
		session.setAttribute("ID", r.getId());
		session.setAttribute("Name", r.getName());
		session.setAttribute("iat", System.currentTimeMillis());
		sessions.put(session.getId(), session);
		return ResponseEntity.ok("Session created");
	}
	//Test parameters, ignore for now
	@PostMapping("/test")
	public ResponseEntity<String> test(HttpSession session) {
		session.setAttribute("ID", "test"+x++);
		session.setAttribute("Name", "test"+x++);
		session.setAttribute("iat", System.currentTimeMillis());
		sessions.put(session.getId(), session);
		return ResponseEntity.ok("Test session created");
	}
	//Send an action request to another robot (any message here can be read by the other robot)
	@PostMapping("/send")
	public ResponseEntity<String> getSessionValue(HttpSession session, @RequestBody ActionRequest action) {
		try {
			String fromID = (String) session.getAttribute("ID");
			action.setFromID(fromID); //TODO: Add user checks (check if other session exists)
			actions.add(action);
		}
		catch(Exception e) {
			ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Action failed to send");
		}
		return ResponseEntity.ok("Action sent");
	}
	
	//Get all messages intended for current user
	@GetMapping("/receive")
	public List<ActionRequest> retrieveActions(HttpSession session) {
		String toID = (String) session.getAttribute("ID");
		List<ActionRequest> actions = new ArrayList<ActionRequest>();
		for(int i =0; i < MainAPI.actions.size(); i++) {
			if(MainAPI.actions.get(i).getToID().equals(toID)) {
				actions.add(MainAPI.actions.get(i));
			}
		}
		return actions;
	}
	
	//Lists all active sessions (could be useful for debugging and monitoring)
	@GetMapping("/active-sessions")
	public ArrayList<String> getActiveSessions() {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, HttpSession> entry : sessions.entrySet()) {
			String key = entry.getKey();
			HttpSession value = entry.getValue();
			list.add(key + ": " + value);
		}
		return list;
	}

}
