/** 
 *  Copyright (c) 2015 The original author or authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.reveno.atp.core.disruptor;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import org.reveno.atp.api.EventsManager.EventMetadata;
import org.reveno.atp.api.transaction.EventBus;
import org.reveno.atp.core.api.Destroyable;
import org.reveno.atp.core.api.RestoreableEventBus;
import org.reveno.atp.core.api.TransactionCommitInfo;
import org.reveno.atp.utils.MapUtils;
import sun.misc.Contended;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("rawtypes")
public class ProcessorContext implements Destroyable {
	
	@Contended
	private long time = 0L;
	public long time() {
		return time;
	}
	public ProcessorContext time(long time) {
		this.time = time;
		return this;
	}

	@Contended
	private boolean skipViews = false;
	public boolean isSkipViews() {
		return skipViews;
	}
	public ProcessorContext skipViews() {
		this.skipViews = true;
		return this;
	}

	private boolean isSystem = false;
	public boolean isSystem() {
		return isSystem;
	}
	@Contended
	private long systemFlag = 0L;
	public long systemFlag() {
		return systemFlag;
	}
	public ProcessorContext systemFlag(long systemFlag) {
		this.isSystem = true;
		this.systemFlag = systemFlag;
		return this;
	}
	
	private final RestoreableEventBus defaultEventBus = new ProcessContextEventBus();
	public EventBus defaultEventBus() {
		return defaultEventBus;
	}
	
	@Contended
	private long transactionId; 
	public long transactionId() {
		return transactionId;
	}
	public ProcessorContext transactionId(long transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	@Contended
	private CompletableFuture future;
	public CompletableFuture future() {
		return future;
	}
	public ProcessorContext future(CompletableFuture future) {
		this.future = future;
		return this;
	}
	
	@Contended
	private boolean hasResult;
	public boolean hasResult() {
		return hasResult;
	}
	public ProcessorContext withResult() {
		this.hasResult = true;
		return this;
	}
	
	@Contended
	private Object commandResult;
	public Object commandResult() {
		return commandResult;
	}
	public void commandResult(Object commandResult) {
		this.commandResult = commandResult;
	}
	
	@Contended
	private boolean isAborted;
	private Throwable abortIssue;
	public boolean isAborted() {
		return isAborted;
	}
	public Throwable abortIssue() {
		return abortIssue;
	}
	public void abort(Throwable abortIssue) {
		this.isAborted = true;
		this.abortIssue = abortIssue;
	}
	
	@Contended
	private boolean isReplicated;
	public boolean isReplicated() {
		return isReplicated;
	}
	public ProcessorContext replicated() {
		isReplicated = true;
		return this;
	}
	
	@Contended
	private boolean isRestore;
	public boolean isRestore() {
		return isRestore;
	}
	public ProcessorContext restore() {
		isRestore = true;
		return this;
	}

	@Contended
	private boolean isSync;
	public boolean isSync() {
		return isSync;
	}
	public ProcessorContext sync() {
		this.isSync = true;
		return this;
	}
	
	@Contended
	private final List<Object> commands = new ArrayList<>();
	public List<Object> getCommands() {
		return commands;
	}
	public ProcessorContext addCommand(Object cmd) {
		commands.add(cmd);
		return this;
	}
	public ProcessorContext addCommands(List<Object> cmds) {
		commands.addAll(cmds);
		return this;
	}
	
	@Contended
	private final List<Object> transactions = new ArrayList<>();
	public List<Object> getTransactions() {
		return transactions;
	}
	public ProcessorContext addTransactions(List<Object> transactions) {
		this.transactions.addAll(transactions);
		return this;
	}
	
	@Contended
	private final List<Object> events = new ArrayList<>();
	public List<Object> getEvents() {
		return events;
	}
	
	private RestoreableEventBus eventBus = defaultEventBus;
	public RestoreableEventBus eventBus() {
		return eventBus;
	}
	public ProcessorContext eventBus(RestoreableEventBus eventBus) {
		this.eventBus = eventBus;
		return this;
	}
	
	@Contended
	private EventMetadata eventMetadata;
	public EventMetadata eventMetadata() {
		return eventMetadata;
	}
	public ProcessorContext eventMetadata(EventMetadata eventMetadata) {
		this.eventMetadata = eventMetadata;
		return this;
	}

	private Map<Class<?>, Long2ObjectLinkedOpenHashMap<Object>> markedRecords = MapUtils.linkedFastRepo();
	public Map<Class<?>, Long2ObjectLinkedOpenHashMap<Object>> getMarkedRecords() {
		return markedRecords;
	}
	public ProcessorContext setMarkedRecords(Map<Class<?>, Long2ObjectLinkedOpenHashMap<Object>> markedRecords) {
		this.markedRecords = markedRecords;
		return this;
	}

	@Contended
	private final TransactionCommitInfo commitInfo;
	public TransactionCommitInfo commitInfo() {
		return commitInfo;
	}
	
	public ProcessorContext reset() {
		transactionId = 0L;
		systemFlag = 0L;
		isSystem = false;
		skipViews = false;
		commands.clear();
		transactions.clear();
		events.clear();
		markedRecords.values().forEach(Long2ObjectLinkedOpenHashMap::clear);
		hasResult = false;
		isAborted = false;
		isSync = false;
		isRestore = false;
		isReplicated = false;
		abortIssue = null;
		future = null;
		commandResult = null;
		eventMetadata = null;
		eventBus = defaultEventBus;
		
		return this;
	}
	
	public void destroy() {
		reset();
	}
	
	public ProcessorContext(TransactionCommitInfo commitInfo) {
		this.commitInfo = commitInfo;
	}
	
	protected class ProcessContextEventBus implements RestoreableEventBus {
		@Override
		public void publishEvent(Object event) {
			ProcessorContext.this.getEvents().add(event);
		}

		@Override
		public RestoreableEventBus currentTransactionId(long transactionId) {
			return this;
		}

		@Override
		public RestoreableEventBus underlyingEventBus(EventBus eventBus) {
			return this;
		}
	}
	
}
