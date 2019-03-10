package com.foundaml.server.models

sealed trait Backend

case class Local() extends Backend
case class TensorFlowBackend(host: String, port: String)
