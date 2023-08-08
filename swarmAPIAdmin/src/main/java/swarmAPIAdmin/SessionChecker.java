package swarmAPIAdmin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
@Service
public class SessionChecker {
	@Value("${custom-session-expiry}")
    private long sessionExpiry;
	//Just checks all running session and removes the expired ones
	@Scheduled(fixedDelayString = "#{${custom-session-expiry}}") // 15 minutes in milliseconds
	public void updateListPeriodically() {
		for(String key : MainAPI.sessions.keySet()) {
			long iat = (long) MainAPI.sessions.get(key).getAttribute("iat");
			if(iat + sessionExpiry <= System.currentTimeMillis()) {
				HttpSession s = MainAPI.sessions.remove(key);
				System.out.println("Removed sessions: "+ s.getId());
			}
		}
		System.out.println("Refreshed sessions at " + System.currentTimeMillis());
	}

}
