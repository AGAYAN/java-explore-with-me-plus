package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompilationParam {

  private  Boolean pinned;

  private int from = 0;

  private int size = 10;

}
