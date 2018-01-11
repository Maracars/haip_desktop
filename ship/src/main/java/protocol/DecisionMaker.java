package protocol;

import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.StatusType;

import java.util.*;

public class DecisionMaker {
	private static Map<StatusType, List<ActionType>> decisionMap;
	private static Map<ActionType, StatusType> objectiveMap;

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

	private static void initializeObjectiveMap() {
		objectiveMap.put(ActionType.ENTER, StatusType.PARKING);
		objectiveMap.put(ActionType.LEAVE, StatusType.SEA);
		objectiveMap.put(ActionType.IDLE, null);

	}

	public static Status getRandomAction(StatusType status) {
		List<ActionType> actionList = decisionMap.get(status);
		ActionType action = actionList.get(new Random().nextInt(actionList.size()));
		StatusType finalStatus = ((objectiveMap.get(action)) == null ? status : objectiveMap.get(action));
		return new Status(finalStatus.toString(), action.toString());
	}

	public static Status getNewPossibleAction(ActionType action) {
		StatusType status = objectiveMap.get(action);
		return new Status(status.toString(), action.toString());
	}

}
