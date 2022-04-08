import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import imgURL from '../logo.png';

const useStyles = makeStyles((theme) => ({
  logo: {
    height: 55,
    marginRight: 30,
  },
}));

export default function AppLogo() {
  const classes = useStyles();
  return <img src={imgURL} alt="Conductor" className={classes.logo} />;
}
