package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompilationParam {

  private  Boolean pinned;

  private int from;

  private int size;

}
