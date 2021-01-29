package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;

public interface LockableObject {
    HTMContainerLock getLock();
    void setLock(HTMContainerLock lock);
}
