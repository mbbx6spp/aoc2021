{ nixpkgs ? import <nixpkgs> {}
}:
let
  inherit (nixpkgs) pkgs;
  inherit (pkgs) mkShell;
in mkShell {
  buildInputs = with pkgs; [
    openjdk11
    scala
    sbt
    sbt-extras
    coursier
    bloop
    metals
    entr
  ];
}
