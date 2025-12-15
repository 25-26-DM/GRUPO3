/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.data.posts.impl

import com.example.jetnews.R
import com.example.jetnews.model.Markup
import com.example.jetnews.model.MarkupType
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Paragraph
import com.example.jetnews.model.ParagraphType
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.model.Publication

/**
 * Define hardcoded posts to avoid handling any non-ui operations.
 */

val alexis = PostAuthor("Alexis","Carvajal", "07/04/1999","Alameda","0999880841")
val joel = PostAuthor("Joel","Guamangallo", "14/10/2002","Calderon","0960090194")
val damian = PostAuthor("Damian","Minda", "01/05/2003","Chillogallo, Barrio Quito Occidente","0980353114")
val billy = PostAuthor("Billy","Moreno", "00/00/1000"," Quito","0900394055")
val david = PostAuthor("David","Ortega", "19/09/2003"," Tumbaco, La Morita","0994906177")
val jostyn = PostAuthor("Jostyn","Palacios", "24/09/2003","Las Casas","0967318322")

val publication = Publication(
    "Android Developers",
    "https://cdn-images-1.medium.com/max/258/1*u7oZc2_5mrkcFaxkXEyfYA@2x.png"
)
val paragraphsPost1 = listOf(
    Paragraph(
        ParagraphType.CodeBlock,
        "Nombre: " + alexis.name + "\n" +
                "Apellido: " + alexis.lastName + "\n" +
                "Fecha de Nacimiento: " + alexis.birthDay + "\n" +
                "Dirección : " + alexis.direction + "\n" +
                "Nombre: " + alexis.telephony
    )
)

val paragraphsPost2 = listOf(
    Paragraph(
        ParagraphType.CodeBlock,
        "Nombre: " + joel.name + "\n" +
                "Apellido: " + joel.lastName + "\n" +
                "Fecha de Nacimiento: " + joel.birthDay + "\n" +
                "Dirección : " + joel.direction + "\n" +
                "Nombre: " +    joel.telephony
    )
)
val paragraphsPost3 = listOf(
    Paragraph(
        ParagraphType.CodeBlock,
        "Nombre: " + damian.name + "\n" +
                "Apellido: " + damian.lastName + "\n" +
                "Fecha de Nacimiento: " + damian.birthDay + "\n" +
                "Dirección : " + damian.direction + "\n" +
                "Nombre: " + damian.telephony
    )
)
val paragraphsPost4 = listOf(
    Paragraph(
        ParagraphType.CodeBlock,
        "Nombre: " + billy.name + "\n" +
                "Apellido: " + billy.lastName + "\n" +
                "Fecha de Nacimiento: " + billy.birthDay + "\n" +
                "Dirección : " + billy.direction + "\n" +
                "Nombre: " + billy.telephony
    )
)
val paragraphsPost5 = listOf(
    Paragraph(
        ParagraphType.CodeBlock,
        "Nombre: " + david.name + "\n" +
                "Apellido: " + david.lastName + "\n" +
                "Fecha de Nacimiento: " + david.birthDay + "\n" +
                "Dirección : " + david.direction + "\n" +
                "Nombre: " + david.telephony
    )
)

val paragraphsPost6 = listOf(
    Paragraph(
        ParagraphType.CodeBlock,
        "Nombre: " + jostyn.name + "\n" +
                "Apellido: " +  jostyn.lastName + "\n" +
                "Fecha de Nacimiento: " + jostyn.birthDay + "\n" +
                "Dirección : " + jostyn.direction + "\n" +
                "Nombre: " + jostyn.telephony
    )
)

val post1 = Post(
    id = "dc523f0ed25c",
    title = "Alexis Carvajal",
    subtitle = "Detalles del estudiante.",
    url = "https://medium.com/androiddevelopers/gradle-path-configuration-dc523f0ed25c",
    publication = publication,
    metadata = Metadata(
        author = alexis,
        date = "August 02",
        readTimeMinutes = 1
    ),
    paragraphs = paragraphsPost1,
    imageId = R.drawable.post_1,
    imageThumbId = R.drawable.post_1_thumb
)

val post2 = Post(
    id = "7446d8dfd7dc",
    title = "Joel Guamangallo",
    subtitle = "Detalles del estudiante.",
    url = "https://medium.com/androiddevelopers/dagger-in-kotlin-gotchas-and-optimizations-7446d8dfd7dc",
    publication = publication,
    metadata = Metadata(
        author = joel,
        date = "July 30",
        readTimeMinutes = 3
    ),
    paragraphs = paragraphsPost2,
    imageId = R.drawable.post_2,
    imageThumbId = R.drawable.post_2_thumb
)

val post3 = Post(
    id = "ac552dcc1741",
    title = "Damian Minda",
    subtitle = "Detalles del estudiante.",
    url = "https://medium.com/androiddevelopers/from-java-programming-language-to-kotlin-the-idiomatic-way-ac552dcc1741",
    publication = publication,
    metadata = Metadata(
        author = damian,
        date = "July 09",
        readTimeMinutes = 1
    ),
    paragraphs = paragraphsPost3,
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb
)

val post4 = Post(
    id = "84eb677660d9",
    title = "Billy Moreno",
    subtitle = "Detalles del estudiante.",
    url = "https://medium.com/androiddevelopers/locale-changes-and-the-androidviewmodel-antipattern-84eb677660d9",
    publication = publication,
    metadata = Metadata(
        author = billy,
        date = "April 02",
        readTimeMinutes = 1
    ),
    paragraphs = paragraphsPost4,
    imageId = R.drawable.post_4,
    imageThumbId = R.drawable.post_4_thumb
)

val post5 = Post(
    id = "55db18283aca",
    title = "David Ortega",
    subtitle = "Detalles del estudiante.",
    url = "https://medium.com/androiddevelopers/collections-and-sequences-in-kotlin-55db18283aca",
    publication = publication,
    metadata = Metadata(
        author = david,
        date = "July 24",
        readTimeMinutes = 4
    ),
    paragraphs = paragraphsPost5,
    imageId = R.drawable.post_5,
    imageThumbId = R.drawable.post_5_thumb
)

val post6 = Post(
    id = "55db18283acb",
    title = "Jostyn Palacios",
    subtitle = "Detalles del estudiante.",
    url = "https://medium.com/androiddevelopers/collections-and-sequences-in-kotlin-55db18283aca",
    publication = publication,
    metadata = Metadata(
        author = jostyn,
        date = "July 24",
        readTimeMinutes = 4
    ),
    paragraphs = paragraphsPost6,
    imageId = R.drawable.post_5,
    imageThumbId = R.drawable.post_5_thumb
)

val posts: List<Post> =
    listOf(
        post1,
        post2,
        post3,
        post4,
        post5,
        post6,
        post1.copy(id = "post6"),
        post2.copy(id = "post7"),
        post3.copy(id = "post8"),
        post4.copy(id = "post9"),
        post5.copy(id = "post10"),
        post6.copy(id = "post11")
    )
