package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;

import java.util.Optional;

public interface LockableObject {

	Optional<HTMContainerLock> getLock();

	void setLock(HTMContainerLock lock);
}
