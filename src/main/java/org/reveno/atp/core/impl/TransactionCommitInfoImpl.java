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

package org.reveno.atp.core.impl;

import java.util.List;

import org.reveno.atp.core.api.TransactionCommitInfo;

public class TransactionCommitInfoImpl implements TransactionCommitInfo {

	private long transactionId;
	public long transactionId() {
		return transactionId;
	}
	public TransactionCommitInfo transactionId(final long transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	private int version;
	public int version() {
		return version;
	}
	public TransactionCommitInfo version(final int version) {
		this.version = version;
		return this;
	}

	private long time;
	public long time() {
		return time;
	}
	public TransactionCommitInfo time(final long time) {
		this.time = time;
		return this;
	}
	
	private List<Object> transactionCommits;
	public List<Object> transactionCommits() {
		return transactionCommits;
	}
	public TransactionCommitInfo transactionCommits(List<Object> transactionCommits) {
		this.transactionCommits = transactionCommits;
		return this;
	}
	
	
	public static class PojoBuilder implements TransactionCommitInfo.Builder {
		@Override
		public TransactionCommitInfo create() {
			return new TransactionCommitInfoImpl();
		}
	}
	
}
