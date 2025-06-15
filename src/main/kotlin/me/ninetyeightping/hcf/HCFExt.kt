package me.ninetyeightping.hcf

import me.ninetyeightping.hcf.players.HCFPlayer
import org.bson.Document

/**
 * Class created on 6/15/2025

 * @author Max C.
 * @project testinghcfcore
 * @website https://solo.to/redis
 */

fun Document.toHCFPlayer(): HCFPlayer = HCF.instance.gson.fromJson(this.toString(), HCFPlayer::class.java)