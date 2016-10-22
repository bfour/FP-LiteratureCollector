/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.domain.builders;

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;

public class ProtocolEntryBuilder extends EntityBuilder<ProtocolEntry>
		implements Builder<ProtocolEntry> {

	private String string;

	public ProtocolEntryBuilder() {
		super();
	}

	public ProtocolEntryBuilder(ProtocolEntry entry) {
		setID(entry.getID());
		setCreationTime(entry.getCreationTime());
		setLastChangeTime(entry.getLastChangeTime());
		setString(entry.getString());
	}

	@Override
	public ProtocolEntry getObject() {
		return new ProtocolEntry(getID(), getCreationTime(), getLastChangeTime(),
				getString());
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

}
