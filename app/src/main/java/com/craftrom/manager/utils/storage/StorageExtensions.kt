package com.craftrom.manager.utils.storage

import com.craftrom.manager.utils.root.RootUtils.getProp

val isDiskEncrypted = getProp("ro.crypto.state").equals("encrypted", true)