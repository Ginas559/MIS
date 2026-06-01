package vn.iotstar.coolenglish.dao.impl;

import vn.iotstar.coolenglish.entity.Room;

public class RoomDAO extends AbstractDAO<Room> {

	@Override
	protected void validateEntity(Room entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Room entity is required.");
		}
	}
}

