/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.voltariuss.diagonia.model.entity.converter;

import com.google.gson.Gson;
import fr.voltariuss.diagonia.model.service.api.dto.LocationDto;
import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import org.jetbrains.annotations.NotNull;

public class LocationDtoConverter implements AttributeConverter<LocationDto, String> {

  @Override
  public @NotNull String convertToDatabaseColumn(@Nullable LocationDto locationDto) {
    Gson gson = new Gson();
    return gson.toJson(locationDto);
  }

  @Override
  public @Nullable LocationDto convertToEntityAttribute(@Nullable String locationJson) {
    Gson gson = new Gson();
    return gson.fromJson(locationJson, LocationDto.class);
  }
}
