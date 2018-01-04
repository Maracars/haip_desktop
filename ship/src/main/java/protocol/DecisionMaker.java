package protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.StatusType;

public class DecisionMaker {
	
	static Map<StatusType, List<ActionType>> decisionMap;
	static Map<ActionType, StatusType> objectiveMap;
	
	static {
		decisionMap = new HashMap<>();
		objectiveMap = new HashMap<>();
		initializeDecisionMap();
		initializeObjectiveMap();
	}
	
	private static void initializeDecisionMap() {
		List<ActionType> actionList = new ArrayList<>();
		actionList.add(ActionType.LEAVE);
		actionList.add(ActionType.IDLE);
		decisionMap.put(StatusType.PARKING, actionList);
		actionList = new ArrayList<>();
		actionList.add(ActionType.ENTER);
		actionList.add(ActionType.IDLE);
		decisionMap.put(StatusType.SEA, actionList);
	}
	
	private static void initializeObjectiveMap () {
		objectiveMap.put(ActionType.ENTER, StatusType.PARKING);
		objectiveMap.put(ActionType.LEAVE, StatusType.SEA);
		objectiveMap.put(ActionType.IDLE, null);
		
	}
	public static Status getRandomAction(StatusType status) {
		List<ActionType> actionList = decisionMap.get(status);
		ActionType action = actionList.get(new Random().nextInt(actionList.size()));
		System.out.println("ACTION: "+action.name());
		StatusType finalStatus = ((objectiveMap.get(action)) == null ? status : objectiveMap.get(action));
		return new Status(finalStatus.toString(), action.toString());
		
	}

}
